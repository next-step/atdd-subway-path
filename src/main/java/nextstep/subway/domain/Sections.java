package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.SectionDuplicationStationException;
import nextstep.subway.exception.SectionNotConnectingStationException;
import nextstep.subway.exception.SectionRemoveLastStationException;
import nextstep.subway.exception.SectionRemoveSizeException;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Section> sections;

	public Sections() {
		this.sections = new ArrayList<>();
	}

	public void addSection(Section section) {
		if (!sections.isEmpty()) {
			validateConnectingStation(section.getUpStation());
			validateDuplicationStation(section.getDownStation());
		}

		this.sections.add(section);
	}

	private void validateConnectingStation(Station station) {
		if (!getLastSection().isSameDownStation(station.getId())) {
			throw new SectionNotConnectingStationException();
		}
	}

	private void validateDuplicationStation(Station station) {
		if (sections.stream()
			.anyMatch(section -> section.isSameUpStation(station.getId()))) {
			throw new SectionDuplicationStationException();
		}
	}

	private void addLastStation(List<Station> stations) {
		if (sections.size() > 0) {
			Section lastSection = getLastSection();
			stations.add(lastSection.getDownStation());
		}
	}

	private Section getLastSection() {
		return sections.get(sections.size() - 1);
	}

	public void removeSection(Long stationId) {
		validateSectionSize();
		validateLastSection(stationId);
		sections.remove(getLastSection());
	}

	private void validateSectionSize() {
		if (sections.size() < 2) {
			throw new SectionRemoveSizeException();
		}
	}

	private void validateLastSection(Long stationId) {
		if (!getLastSection().isSameDownStation(stationId)) {
			throw new SectionRemoveLastStationException();
		}
	}

	public List<Station> getStations() {
		if (sections.isEmpty()) {
			return Collections.emptyList();
		}

		Map<Station, Section> stationSectionMap = sections.stream()
			.collect(Collectors.toMap(Section::getUpStation, Function.identity()));
		return createStations(stationSectionMap);
	}

	private List<Station> createStations(Map<Station, Section> stationSectionMap) {
		List<Station> stations = new ArrayList<>();
		Station upTerminalStation = getUpTerminalStation();
		stations.add(upTerminalStation);

		while (stationSectionMap.containsKey(upTerminalStation)) {
			Station downStation = stationSectionMap.get(upTerminalStation).getDownStation();
			upTerminalStation = downStation;
			stations.add(downStation);
		}
		return stations;
	}

	private Station getUpTerminalStation() {
		List<Station> upStations = sections.stream()
			.map(Section::getUpStation)
			.collect(Collectors.toUnmodifiableList());
		return upStations.stream()
			.filter(s -> sections.stream()
				.noneMatch(section -> section.isSameDownStation(s.getId())))
			.findAny()
			.orElseThrow();
	}
}
