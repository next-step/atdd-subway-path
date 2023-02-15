package nextstep.subway.domain;

import nextstep.subway.ui.error.exception.BusinessException;
import nextstep.subway.ui.error.exception.EntityNotFoundException;
import nextstep.subway.ui.error.exception.InvalidValueException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.ui.error.exception.ErrorCode.*;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public List<Section> getAllSections() {
		return sections;
	}

	public void addSection(Line line, Station upStation, Station downStation, int distance) {
		if (sections.isEmpty()) {
			sections.add(new Section(line, upStation, downStation, distance));
			return;
		}

		for (Section section : line.getSections()) {
			if (section.getDownStation().equals(downStation) || section.getUpStation().equals(downStation)) {
				throw new InvalidValueException(ALREADY_REGISTERED_STAION);
			}
		}

		sections.stream().filter(s -> s.getUpStation().equals(upStation))
				.findFirst()
				.ifPresentOrElse(section -> {
							splitSection(line, downStation, distance, section);
						}, () -> {
							sections.add(new Section(line, upStation, downStation, distance));
						}
				);
	}

	public void remove(Line line, Station station) {

		if (sections.size() <= 1) {
			throw new BusinessException(SECTION_NOT_DELETE_THEN_ONE);
		}

		Station targetStation = getAllStations().stream()
				.filter(station::equals)
				.findFirst()
				.orElseThrow(() -> new EntityNotFoundException(STATION_NOT_EXISTS));

		List<Section> targetSections = line.getSections().stream()
				.filter(section -> section.getUpStation().equals(targetStation) || section.getDownStation().equals(targetStation))
				.collect(Collectors.toList());

		if (targetSections.size() > 1) {
			Section firstSection = targetSections.get(0);
			Section secondSection = targetSections.get(1);
			this.sections.add(new Section(line, firstSection.getUpStation(), secondSection.getDownStation(), firstSection.getDistance() + secondSection.getDistance()));
			this.sections.removeIf(section -> section.equals(targetSections.get(0)) || section.equals(targetSections.get(1)));
			return;
		}

		this.sections.removeIf(section -> section.equals(targetSections.get(0)));
	}

	public List<Station> getStations() {
		return this.sections.stream()
				.map(s -> List.of(s.getUpStation(), s.getDownStation()))
				.flatMap(Collection::stream)
				.distinct()
				.collect(Collectors.toList());
	}

	private void splitSection(Line line, Station downStation, int distance, Section oldSection) {
		Station oldUpstation = oldSection.getUpStation();
		Station oldDownStation = oldSection.getDownStation();
		int oldDistance = oldSection.getDistance();

		if (distance >= oldDistance) {
			throw new InvalidValueException(SECTION_NOT_LONGER_THEN_EXISTING_SECTION);
		}


		sections.remove(oldSection);

		sections.addAll(List.of(
				new Section(line, downStation, oldDownStation, oldDistance - distance),
				new Section(line, oldUpstation, downStation, distance)
		));
	}

	private List<Station> getAllStations() {
		List<Station> stations = new ArrayList<>();

		if (this.sections.isEmpty()) {
			return stations;
		}

		stations.add(this.sections.get(0).getUpStation());
		for (Section section : this.sections) {
			stations.add(section.getDownStation());
		}

		return stations;
	}
}
