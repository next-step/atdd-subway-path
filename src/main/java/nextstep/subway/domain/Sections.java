package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.CannotCreateSectionException;
import nextstep.subway.exception.CannotFindFinalStation;
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

		if (hasNewMiddleStationConnectedUpStation(section)) {
			addSectionHasNewMiddleStationConnectedUpStation(section);
			return;
		}
		if (hasNewMiddleStationConnectedDownStation(section)) {
			addSectionHasNewMiddleStationConnectedDownStation(section);
			return;
		}

		this.sections.add(section);
	}

	private void validateSection(Section section) {
		List<Station> stations = getStations();
		if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
			throw new CannotCreateSectionException(ErrorMessage.SHOULD_EXIST_NEW_STATION);
		}
		if (!stations.isEmpty() && !stations.contains(section.getUpStation()) && !stations.contains(
			section.getDownStation())) {
			throw new CannotCreateSectionException(ErrorMessage.SHOULD_BE_INCLUDE_UP_STATION_OR_DOWN_STATION);
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

	private boolean hasNewMiddleStationConnectedUpStation(Section section) {
		return !sections.isEmpty() && getFinalUpStation(getDownStations()).equals(section.getUpStation());
	}

	private boolean hasNewMiddleStationConnectedDownStation(Section section) {
		return !sections.isEmpty() && getFinalDownStation(getUpStations()).equals(section.getDownStation());
	}

	public List<Station> getStations() {
		if (sections.isEmpty()) {
			return Collections.emptyList();
		}
		Station finalUpStation = getFinalUpStation(getDownStations());
		Station finalDownStation = getFinalDownStation(getUpStations());

		List<Station> stations = new ArrayList<>();
		stations.add(finalUpStation);

		Section nextSection = findSectionByUpStation(finalUpStation);
		while (!nextSection.getDownStation().equals(finalDownStation)) {
			stations.add(nextSection.getDownStation());
			nextSection = findSectionByUpStation(nextSection.getDownStation());
		}
		stations.add(finalDownStation);

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

	private List<Station> getUpStations() {
		return sections.stream()
			.map(Section::getUpStation)
			.collect(Collectors.toList());
	}

	private List<Station> getDownStations() {
		return sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());
	}

	private Station getFinalUpStation(List<Station> downStations) {
		return getUpStations().stream()
			.filter(station -> !downStations.contains(station))
			.findFirst()
			.orElseThrow(() -> new CannotFindFinalStation(ErrorMessage.CANNOT_FIND_FINAL_DOWN_STATION));
	}

	private Station getFinalDownStation(List<Station> upStations) {
		return getDownStations().stream()
			.filter(station -> !upStations.contains(station))
			.findFirst()
			.orElseThrow(() -> new CannotFindFinalStation(ErrorMessage.CANNOT_FIND_FINAL_UP_STATION));

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
