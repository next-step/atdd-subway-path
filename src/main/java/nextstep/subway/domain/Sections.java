package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.SectionDuplicationStationException;
import nextstep.subway.exception.SectionLastRemoveException;
import nextstep.subway.exception.SectionLongDistanceException;
import nextstep.subway.exception.SectionNotIncludedException;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Section> sections;

	public Sections() {
		this.sections = new ArrayList<>();
	}

	public Sections(List<Section> sections) {
		this.sections = sections;
	}

	public void addSection(Section section) {
		if (sections.isEmpty()) {
			this.sections.add(section);
			return;
		}

		validateAddSection(section);
		addSectionWhenOriginUpStationEqualsNewUpStation(section);
		addSectionWhenOriginDawnStationEqualsNewDownStation(section);
		this.sections.add(section);
	}

	private void validateAddSection(Section section) {
		List<Station> stations = getStations();
		boolean containsUpStation = stations.contains(section.getUpStation());
		boolean containsDownStation = stations.contains(section.getDownStation());

		if (containsUpStation && containsDownStation) {
			throw new SectionDuplicationStationException();
		}

		if (!(containsUpStation || containsDownStation)) {
			throw new SectionNotIncludedException();
		}
	}

	private void addSectionWhenOriginUpStationEqualsNewUpStation(Section section) {
		sections.stream()
			.filter(s -> s.isSameUpStation(section.getUpStation().getId()))
			.findAny()
			.ifPresent(s -> {
				sections.add(new Section(section.getLine(), section.getDownStation(), s.getDownStation(),
					calculateDistance(s.getDistance(), section.getDistance())));
				sections.remove(s);
			});
	}

	private void addSectionWhenOriginDawnStationEqualsNewDownStation(Section section) {
		sections.stream()
			.filter(s -> s.isSameDownStation(section.getDownStation().getId()))
			.findAny()
			.ifPresent(s -> {
				sections.add(new Section(section.getLine(), s.getUpStation(), section.getUpStation(),
					calculateDistance(s.getDistance(), section.getDistance())));
				sections.remove(s);
			});
	}

	private int calculateDistance(int originalDistance, int newDistance) {
		if (originalDistance <= newDistance) {
			throw new SectionLongDistanceException();
		}

		return originalDistance - newDistance;
	}

	public void removeSection(Long stationId) {
		validateRemoveSection(stationId);
		Section upSection = findUpSectionByStationId(stationId);
		Section downSection = findDownSectionByStationId(stationId);
		removeSectionWhenSectionNotEmpty(upSection, downSection);
		addSectionWhenUpSectionAndDownSectionNotEmpty(upSection, downSection);
	}

	private void validateRemoveSection(Long stationId) {
		if (sections.size() < 2) {
			throw new SectionLastRemoveException();
		}

		boolean notExistStationId = getStations().stream()
			.map(Station::getId)
			.noneMatch(id -> id.equals(stationId));

		if (notExistStationId) {
			throw new SectionNotIncludedException();
		}

	}

	private void removeSectionWhenSectionNotEmpty(Section upSection, Section downSection) {
		if (Objects.nonNull(upSection)) {
			sections.remove(upSection);
		}

		if (Objects.nonNull(downSection)) {
			sections.remove(downSection);
		}
	}

	private void addSectionWhenUpSectionAndDownSectionNotEmpty(Section upSection, Section downSection) {
		if (upSection == null || downSection == null) {
			return;
		}

		sections.add(new Section(
			upSection.getLine(),
			downSection.getUpStation(),
			upSection.getDownStation(),
			upSection.getDistance() + downSection.getDistance()
		));
	}

	private Section findUpSectionByStationId(Long stationId) {
		return sections.stream()
			.filter(s -> s.isSameUpStation(stationId))
			.findAny()
			.orElse(null);
	}

	private Section findDownSectionByStationId(Long stationId) {
		return sections.stream()
			.filter(s -> s.isSameDownStation(stationId))
			.findAny()
			.orElse(null);
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

	public List<Section> getSections() {
		return sections;
	}

	public int getTotalDistance() {
		return (int)sections.stream()
			.mapToLong(Section::getDistance)
			.sum();
	}
}
