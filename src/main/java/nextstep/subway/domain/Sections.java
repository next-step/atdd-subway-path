package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.CannotCreateSectionException;
import nextstep.subway.exception.CannotFindSectionException;
import nextstep.subway.exception.ErrorMessage;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	protected Sections() {
	}

	public List<Section> getSections() {
		return sections;
	}

	public void addSection(Section section) {
		validateSection(section);

		this.sections.add(section);

		if (hasNewMiddleStationConnectedUpStation(section.getUpStation())) {
			addSectionHasNewMiddleStationConnectedUpStation(section);
		}
		if (hasNewMiddleStationConnectedDownStation(section.getDownStation())) {
			addSectionHasNewMiddleStationConnectedDownStation(section);
		}
	}

	private void validateSection(Section section) {
		List<Station> stations = getStations();
		if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
			throw new CannotCreateSectionException(ErrorMessage.SHOULD_EXIST_NEW_STATION);
		}
		if (!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
			throw new CannotCreateSectionException(ErrorMessage.ALREADY_EXISTED_STATION);
		}
	}

	private void addSectionHasNewMiddleStationConnectedUpStation(Section newSection) {
		Section existingSection = findSectionByUpStation(newSection.getUpStation());

		sections.add(new Section(newSection.getLine(), newSection.getUpStation(), newSection.getDownStation(),
			newSection.getDistance()));
		sections.add(new Section(newSection.getLine(), newSection.getDownStation(), existingSection.getDownStation(),
			existingSection.getDistance() - newSection.getDistance()));

		sections.remove(existingSection);
	}

	private void addSectionHasNewMiddleStationConnectedDownStation(Section newSection) {
		Section existingSection = findSectionByUpStation(newSection.getUpStation());

		sections.add(new Section(newSection.getLine(), newSection.getUpStation(), newSection.getDownStation(),
			newSection.getDistance()));
		sections.add(new Section(newSection.getLine(), existingSection.getUpStation(), newSection.getUpStation(),
			existingSection.getDistance() - newSection.getDistance()));

		sections.remove(existingSection);
	}

	private boolean hasNewMiddleStationConnectedUpStation(Station upStation) {
		return getUpStation().equals(upStation);
	}

	private boolean hasNewMiddleStationConnectedDownStation(Station downStation) {
		return getDownStation().equals(downStation);
	}

	public List<Station> getStations() {
		Station upStation = getUpStation();
		Station downStation = getDownStation();

		List<Station> stations = new ArrayList<>();

		stations.add(upStation);
		while (true) {
			Section section = findSectionByUpStation(upStation);
			stations.add(section.getDownStation());
			if (section.getDownStation().equals(downStation)) {
				break;
			}
		}

		return stations;
	}

	private Section findSectionByUpStation(Station station) {
		Section section = null;
		for (Section next : sections) {
			if (next.getUpStation().equals(station)) {
				section = next;
			}
		}
		return Optional.ofNullable(section).orElseThrow(
			() -> new CannotFindSectionException(ErrorMessage.CANNOT_FIND_SECTION));
	}

	private Station getDownStation() {
		Station station = sections.get(0).getDownStation();

		Optional<Station> downStation = findDownStation(station);

		while (downStation.isPresent()) {
			station = downStation.get();
			downStation = findDownStation(station);
		}

		return station;
	}

	private Station getUpStation() {
		Station station = sections.get(0).getUpStation();

		Optional<Station> upStation = findUpStation(station);

		while (upStation.isPresent()) {
			station = upStation.get();
			upStation = findUpStation(station);
		}

		return station;
	}

	private Optional<Station> findUpStation(Station station) {
		Station upStation = null;
		for (Section section : sections) {
			if (section.getDownStation().equals(station)) {
				upStation = section.getDownStation();
			}
		}
		return Optional.ofNullable(upStation);
	}

	private Optional<Station> findDownStation(Station station) {
		Station downStation = null;
		for (Section section : sections) {
			if (section.getUpStation().equals(station)) {
				downStation = section.getUpStation();
			}
		}
		return Optional.ofNullable(downStation);
	}

	private int getLastIndexOfSections() {
		return sections.size() - 1;
	}

	public boolean isLastDownStation(Station station) {
		return sections.get(getLastIndexOfSections()).getDownStation().equals(station);
	}

	public void removeLastSection() {
		sections.remove(getLastIndexOfSections());
	}
}
