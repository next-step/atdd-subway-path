package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.CannotCreateSectionException;
import nextstep.subway.exception.CannotFindFinalStationException;
import nextstep.subway.exception.CannotFindSectionException;
import nextstep.subway.exception.CannotRemoveSectionException;
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

	public void removeSection(Station station) {
		if (hasSingleSection()) {
			throw new CannotRemoveSectionException(ErrorMessage.CANNOT_REMOVE_SINGLE_SECTION);
		}
		if (notContains(station)) {
			throw new CannotRemoveSectionException(ErrorMessage.CANNOT_REMOVE_NO_EXISTED_STATION);
		}

		if (isMiddleStation(station)) {
			final Section frontSection = findSectionByDownStation(station);
			final Section backSection = findSectionByUpStation(station);
			final Station upStation = frontSection.getUpStation();
			final Station downStation = backSection.getDownStation();
			sections.remove(frontSection);
			sections.remove(backSection);
			connect(frontSection.getLine(), upStation, downStation,
				frontSection.getDistance() + backSection.getDistance());
		}

		if (isFinalUpStation(station)) {
			final Section section = findSectionByUpStation(station);
			sections.remove(section);
		}
		if (isFinalDownStation(station)) {
			final Section section = findSectionByDownStation(station);
			sections.remove(section);
		}
	}

	private void connect(Line line, Station upStation, Station downStation, int distance) {
		sections.add(new Section(line, upStation, downStation, distance));
	}

	private boolean isFinalDownStation(Station station) {
		return Objects.equals(station, getFinalDownStation());
	}

	private boolean isFinalUpStation(Station station) {
		return Objects.equals(station, getFinalUpStation());
	}

	private boolean isMiddleStation(Station station) {
		return !isFinalUpStation(station) && !isFinalDownStation(station);

	}

	private void validateSection(Section section) {
		final List<Station> stations = getStations();
		if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
			throw new CannotCreateSectionException(ErrorMessage.SHOULD_EXIST_NEW_STATION);
		}
		if (!stations.isEmpty() && !stations.contains(section.getUpStation()) && !stations.contains(
			section.getDownStation())) {
			throw new CannotCreateSectionException(ErrorMessage.SHOULD_BE_INCLUDE_UP_STATION_OR_DOWN_STATION);
		}
	}

	private void addSectionHasNewMiddleStationConnectedUpStation(Section newSection) {
		final Section existingSection = findSectionByUpStation(newSection.getUpStation());

		sections.add(new Section(newSection.getLine(), newSection.getUpStation(), newSection.getDownStation(),
			newSection.getDistance()));
		sections.add(new Section(newSection.getLine(), newSection.getDownStation(), existingSection.getDownStation(),
			existingSection.getDistance() - newSection.getDistance()));

		sections.remove(existingSection);
	}

	private void addSectionHasNewMiddleStationConnectedDownStation(Section newSection) {
		final Section existingSection = findSectionByUpStation(newSection.getUpStation());

		sections.add(new Section(newSection.getLine(), newSection.getUpStation(), newSection.getDownStation(),
			newSection.getDistance()));
		sections.add(new Section(newSection.getLine(), existingSection.getUpStation(), newSection.getUpStation(),
			existingSection.getDistance() - newSection.getDistance()));

		sections.remove(existingSection);
	}

	private boolean hasNewMiddleStationConnectedUpStation(Section section) {
		return !sections.isEmpty() && getFinalUpStation().equals(section.getUpStation());
	}

	private boolean hasNewMiddleStationConnectedDownStation(Section section) {
		return !sections.isEmpty() && getFinalDownStation().equals(section.getDownStation());
	}

	public List<Station> getStations() {
		if (sections.isEmpty()) {
			return Collections.emptyList();
		}
		final Station finalUpStation = getFinalUpStation();
		final Station finalDownStation = getFinalDownStation();

		final List<Station> stations = new ArrayList<>();
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
		return sections.stream()
			.filter(section -> section.getUpStation().equals(station))
			.findFirst()
			.orElseThrow(() -> new CannotFindSectionException(ErrorMessage.CANNOT_FIND_SECTION));
	}

	private Section findSectionByDownStation(Station station) {
		return sections.stream()
			.filter(section -> section.getDownStation().equals(station))
			.findFirst()
			.orElseThrow(() -> new CannotFindSectionException(ErrorMessage.CANNOT_FIND_SECTION));
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

	private Station getFinalUpStation() {
		final List<Station> downStations = getDownStations();
		return getUpStations().stream()
			.filter(station -> !downStations.contains(station))
			.findFirst()
			.orElseThrow(() -> new CannotFindFinalStationException(ErrorMessage.CANNOT_FIND_FINAL_DOWN_STATION));
	}

	private Station getFinalDownStation() {
		final List<Station> upStations = getUpStations();
		return getDownStations().stream()
			.filter(station -> !upStations.contains(station))
			.findFirst()
			.orElseThrow(() -> new CannotFindFinalStationException(ErrorMessage.CANNOT_FIND_FINAL_UP_STATION));

	}

	public boolean hasSingleSection() {
		return sections.size() == 1;
	}

	public boolean notContains(Station station) {
		return !getStations().contains(station);
	}
}
