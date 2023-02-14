package nextstep.subway.section;

import java.util.ArrayList;
import java.util.Collection;
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

	public Section addSection(Section section) { //
		if (isEmptySection()) {
			this.sections.add(section);
			return section;
		}
		checkExistSection(section);

		Section registeredSection = findByUpStation(section.getUpStation());
		if (registeredSection != null) {
			reRegisterSection(registeredSection, section);
		}

		this.sections.add(section);
		return section;
	}

	public boolean isEmptySection() {
		return this.sections.size() == 0 ? true : false;
	}

	private void checkExistSection(Section newSection) {
		if (existAllStation(newSection)) {
			throw new IllegalArgumentException("등록하려는 구간의 상행역과 하행역이 이미 노선에 등록되어 있습니다.");
		}
		if (notExistStation(newSection)) {
			throw new IllegalArgumentException("등록하려는 구간의 상행역과 하행역이 노선에 등록되어 있지 않습니다.");
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

	private void reRegisterSection(Section oldSection, Section newSection) {
		checkDistance(oldSection, newSection);
		oldSection.updateUpStation(newSection.getDownStation(), oldSection.getDistance() - newSection.getDistance());
	}

	private void checkDistance(Section oldSection, Section newSection) {
		if (newSection.getDistance() >= oldSection.getDistance()) {
			throw new IllegalArgumentException("등록하려는 구간의 길이가 기존 구간의 길이보다 크거나 같습니다.");
		}
	}

	public void removeSection(Station station) {
		if (this.sections.size() == 1) {
			throw new IllegalArgumentException("삭제하려는 구간이 노선의 마지막 구간입니다.");
		}

		if (!getSections().get(getSections().size() - 1).getDownStation().equals(station)) {
			throw new IllegalArgumentException();
		}

		getSections().remove(getSections().size() - 1);
	}


	public List<Section> getSections() {
		return sections;
	}

	public List<Station> getAllStation() {
		return this.sections.stream()
			.map(section -> List.of(section.getUpStation(), section.getDownStation()))
			.flatMap(Collection::stream)
			.distinct()
			.collect(Collectors.toList());
	}

	public List<Station> getOrderStation() {
		Station upStation = getFirstStation();
		List<Station> stations = new ArrayList<>();
		Map<Station, Station> stationMap = this.sections.stream().collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));

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

		return upStations.stream()
			.filter(s -> !downStations.contains(s))
			.findFirst().orElse(null);
	}

	public int getDistance() {
		return this.sections.stream().mapToInt(Section::getDistance).sum();
	}
}
