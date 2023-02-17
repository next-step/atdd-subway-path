package nextstep.subway.section;

import static nextstep.subway.common.SubwayErrorMsg.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public void addSection(Section newSection) {
		if (isEmptySection()) {
			this.sections.add(newSection);
			return;
		}
		checkExistSection(newSection);

		Section registeredSection = findByUpStation(newSection.getUpStation());
		if (registeredSection != null) {
			reRegisterSection(registeredSection, newSection);
		}

		this.sections.add(newSection);
	}

	private boolean isEmptySection() {
		return this.sections.size() == 0;
	}

	private void checkExistSection(Section newSection) {
		if (existAllStation(newSection)) {
			throw new IllegalArgumentException(SECTIONS_EXIST_ALL_STATION.isMessage());
		}
		if (notExistStation(newSection)) {
			throw new IllegalArgumentException(SECTIONS_NOT_EXIST_STATION.isMessage());
		}
	}

	private boolean existAllStation(Section section) {
		return getAllStation().contains(section.getUpStation()) && getAllStation().contains(section.getDownStation());
	}

	private boolean notExistStation(Section section) {
		return !getAllStation().contains(section.getUpStation()) && !getAllStation().contains(section.getDownStation());
	}

	private Section findByUpStation(Station upStation) {
		return this.sections.stream()
			.filter(s -> s.getUpStation().equals(upStation))
			.findFirst().orElse(null);
	}

	private Section findByDownStation(Station downStation) {
		return this.sections.stream()
			.filter(s -> s.getDownStation().equals(downStation))
			.findFirst().orElse(null);
	}

	private void reRegisterSection(Section oldSection, Section newSection) {
		checkDistance(oldSection, newSection);
		oldSection.updateUpStation(newSection.getDownStation(), oldSection.getDistance() - newSection.getDistance());
	}

	private void checkDistance(Section oldSection, Section newSection) {
		if (newSection.getDistance() >= oldSection.getDistance()) {
			throw new IllegalArgumentException(SECTIONS_DISTANCE_LENGTH.isMessage());
		}
	}

	public void removeSection(Station station) {
		if (this.sections.size() == 1) {
			throw new IllegalArgumentException(SECTIONS_LAST_SECTION.isMessage());
		}

		Section frontSection = findByDownStation(station);
		Section backSection = findByUpStation(station);
		mergeSection(frontSection, backSection);
	}

	private void mergeSection(Section frontSection, Section backSection) {
		if (frontSection != null && backSection != null) {
			frontSection.updateDownStation(backSection.getDownStation(), backSection.getDistance());
			sections.remove(backSection);
			return;
		}
		sections.remove(frontSection);
		sections.remove(backSection);
	}

	private List<Station> getAllStation() {
		List<Station> stations = new ArrayList<>();
		stations.add(getFirstStation());
		stations.addAll(this.sections.stream().map(Section::getDownStation).collect(Collectors.toList()));
		return stations;
	}

	public List<Station> getOrderStation() {
		Map<Station, Station> stationMap = this.sections.stream()
			.collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));

		Station upStation = getFirstStation();
		List<Station> stations = new ArrayList<>();

		stations.add(upStation);
		while (true) {
			Station nextStation = stationMap.getOrDefault(upStation, null);
			if (nextStation == null) {
				return stations;
			}
			stations.add(nextStation);
			upStation = nextStation;
		}
	}

	private Station getFirstStation() {
		List<Station> upStations = this.sections.stream().map(Section::getUpStation).collect(Collectors.toList());
		List<Station> downStations = this.sections.stream().map(Section::getDownStation).collect(Collectors.toList());

		return upStations.stream().filter(s -> !downStations.contains(s)).findFirst().orElse(null);
	}

	public int getDistance() {
		return this.sections.stream().mapToInt(Section::getDistance).sum();
	}
}
