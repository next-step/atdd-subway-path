package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.exception.AlreadyRegisteredException;
import nextstep.subway.exception.CannotInsertLongerSectionException;
import nextstep.subway.exception.CannotInsertSameDistanceSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static nextstep.subway.exception.ErrorCode.*;

@Embeddable
@Getter
public class Sections {

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	List<Section> sections = new ArrayList<>();

	public boolean isEmpty() {
		return sections.isEmpty();
	}

	public void add(Section section) {
		add(section.getLine(), section.getUpStation(), section.getDownStation(), section.getDistance());
	}

	public void add(Line line, Station upStation, Station downStation, int distance) {

		validateStations(upStation, downStation);

		if (isStartWithUpStation(upStation)) {
			Section section = sections.stream()
					.filter(s -> s.getUpStation().equals(upStation))
					.findFirst()
					.orElseThrow(IllegalArgumentException::new);

			validateDistance(distance, section);

			this.sections.remove(section);
			this.sections.add(new Section(line, downStation, section.getDownStation(), modifiedDistance(section.getDistance(), distance)));
			this.sections.add(new Section(line, upStation, downStation, distance));
			return;
		}

		if (sameAsOriginUpStationAndNewDownStation(downStation)) {
			Section section = sections.stream()
					.filter(s -> s.getUpStation().equals(downStation))
					.findFirst()
					.orElseThrow(IllegalArgumentException::new);

			this.sections.add(sections.indexOf(section), new Section(line, upStation, downStation, distance));
			return;
		}

		this.sections.add(new Section(line, upStation, downStation, distance));
	}

	private void validateStations(Station upStation, Station downStation) {
		boolean hasSameUpStation = sections.stream()
				.anyMatch(s -> s.getUpStation().equals(upStation));
		boolean hasSameDownStation = sections.stream()
				.anyMatch(s -> s.getDownStation().equals(downStation));

		if (hasSameUpStation && hasSameDownStation) {
			throw new AlreadyRegisteredException(CANNOT_REGISTER_ALREADY_REGISTERED_SECTION.getMessage());
		}
	}

	private void validateDistance(int distance, Section section) {
		if (section.getDistance() < distance) {
			throw new CannotInsertLongerSectionException(CANNOT_INSERT_LONGER_SECTION.getMessage());
		}

		if (section.getDistance() == distance) {
			throw new CannotInsertSameDistanceSectionException(CANNOT_INSERT_SAME_DISTANCE_SECTION.getMessage());
		}
	}

	private boolean sameAsOriginUpStationAndNewDownStation(Station downStation) {
		return sections.stream()
				.anyMatch(section -> section.getUpStation().equals(downStation));
	}

	private boolean isStartWithUpStation(Station upStation) {
		return sections.stream().anyMatch(section -> section.getUpStation().equals(upStation));
	}

	private Integer modifiedDistance(Integer origin, Integer insert) {
		return origin - insert;
	}

	public List<Station> getStations() {

		if (isEmpty()) {
			return Collections.emptyList();
		}

		Section firstSection = getFirstSection();
		List<Station> stations = addStations(firstSection);
		addAllStations(firstSection, stations);
		return stations;
	}

	public void removeSection(Station station) {
		if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
			throw new IllegalArgumentException();
		}

		sections.remove(this.getSections().size() - 1);
	}

	private Section getFirstSection() {
		return sections.stream()
				.filter(s -> sections.stream()
						.noneMatch(section -> section.getDownStation().equals(s.getUpStation())))
				.findFirst()
				.orElseThrow(AssertionError::new);
	}

	private boolean hasNext(Station station) {
		return sections.stream()
				.map(Section::getUpStation)
				.anyMatch(s -> s.equals(station));
	}

	private Section findSection(Station station) {
		return sections.stream()
				.filter(s -> s.getUpStation().equals(station))
				.findFirst()
				.orElseThrow(AssertionError::new);
	}

	private List<Station> addStations(Section section) {
		return new ArrayList<>(Arrays.asList(section.getUpStation(), section.getDownStation()));
	}

	private void addAllStations(Section firstSection, List<Station> stations) {
		Section nextSection = firstSection;
		while (hasNext(nextSection.getDownStation())) {
			Section section = findSection(nextSection.getDownStation());
			stations.add(section.getDownStation());
			nextSection = section;
		}
	}
}
