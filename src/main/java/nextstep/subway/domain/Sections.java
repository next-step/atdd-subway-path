package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.exception.*;

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

	public void add(Line line, Station upStation, Station downStation, int distance) {
		validateStations(upStation, downStation);
		addSectionWithCondition(line, upStation, downStation, distance);
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

		if (isOnlyOneSection()) {
			throw new CannotRemoveLastSectionException(CANNOT_REMOVE_LAST_SECTION.getMessage());
		}

		if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
			throw new IllegalArgumentException();
		}

		sections.remove(this.getSections().size() - 1);
	}

	private boolean isOnlyOneSection() {
		return sections.size() == 1;
	}

	private void addSectionWithCondition(Line line, Station upStation, Station downStation, Integer distance) {

		if (isStartWithUpStation(upStation)) {
			Section section = getSectionMatchingUpStation(upStation);
			validateDistance(distance, section);
			addSectionInMiddleWithUpStation(line, downStation, distance, section);
			return;
		}

		if (sameAsOriginDownStationAndNewDownStation(downStation)) {
			Section section = getSectionMatchingDownStation(downStation);
			validateDistance(distance, section);
			addSectionInMiddleWithDownStation(line, upStation, distance, section);
			return;
		}

		if (sameAsOriginUpStationAndNewDownStation(downStation)) {
			Section section = getSectionMatchingUpStation(downStation);
			addSectionInFront(line, upStation, downStation, distance, section);
			return;
		}

		this.sections.add(new Section(line, upStation, downStation, distance));
	}

	private void addSectionInFront(Line line, Station upStation, Station downStation, int distance, Section section) {
		this.sections.add(sections.indexOf(section), new Section(line, upStation, downStation, distance));
	}

	private void addSectionInMiddleWithUpStation(Line line, Station downStation, int distance, Section section) {
		this.sections.remove(section);
		this.sections.add(new Section(line, section.getUpStation(), downStation, distance));
		this.sections.add(new Section(line, downStation, section.getDownStation(), modifiedDistance(section.getDistance(), distance)));
	}

	private void addSectionInMiddleWithDownStation(Line line, Station upStation, int distance, Section section) {
		this.sections.remove(section);
		sections.add(new Section(line, section.getUpStation(), upStation, modifiedDistance(section.getDistance() , distance)));
		sections.add(new Section(line, upStation, section.getDownStation(), distance));
	}

	private void validateStations(Station upStation, Station downStation) {

		if (isAlreadyRegistered(upStation, downStation)) {
			throw new AlreadyRegisteredException(CANNOT_REGISTER_ALREADY_REGISTERED_SECTION.getMessage());
		}


		if (!isEmpty() && !haveRegistered(upStation, downStation)) {
			throw new CannotRegisterWithoutRegisteredStation(CANNOT_REGISTER_WITHOUT_REGISTERED_STATIONS.getMessage());
		}
	}

	private boolean haveRegistered(Station upStation, Station downStation) {
		return hasSameUpStation(upStation) || hasSameDownStation(downStation) ||
				hasSameUpStation(downStation) || hasSameDownStation(upStation);
	}

	private boolean isAlreadyRegistered(Station upStation, Station downStation) {
		return hasSameUpStation(upStation) && hasSameDownStation(downStation);
	}

	private boolean hasSameDownStation(Station station) {
		return sections.stream()
				.anyMatch(s -> s.getDownStation().equals(station));
	}

	private boolean hasSameUpStation(Station station) {
		return sections.stream()
				.anyMatch(s -> s.getUpStation().equals(station));
	}
	private void validateDistance(int distance, Section section) {
		if (section.getDistance() < distance) {
			throw new CannotInsertLongerSectionException(CANNOT_INSERT_LONGER_SECTION.getMessage());
		}

		if (section.getDistance() == distance) {
			throw new CannotInsertSameDistanceSectionException(CANNOT_INSERT_SAME_DISTANCE_SECTION.getMessage());
		}
	}

	private Section getSectionMatchingDownStation(Station station) {
		return sections.stream()
				.filter(s -> s.getDownStation().equals(station))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

	private Section getSectionMatchingUpStation(Station station) {
		return sections.stream()
				.filter(s -> s.getUpStation().equals(station))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

	private boolean sameAsOriginUpStationAndNewDownStation(Station downStation) {
		return hasSameUpStation(downStation);
	}

	private boolean sameAsOriginDownStationAndNewDownStation(Station downStation) {
		return hasSameDownStation(downStation);
	}

	private boolean isStartWithUpStation(Station upStation) {
		return hasSameUpStation(upStation);
	}

	private Integer modifiedDistance(Integer origin, Integer insert) {
		return origin - insert;
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
