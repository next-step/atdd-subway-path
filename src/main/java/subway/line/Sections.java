package subway.line;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;

import subway.station.Station;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private final List<Section> sectionList;

	public Sections() {
		this.sectionList = new ArrayList<>();
	}

	public void add(Section newSection) {
		stationIsCanNotBeTheSame(newSection);

		// 최초 등록
		if (sectionList.isEmpty()) {
			sectionList.add(newSection);
			return;
		}

		checkExistingStation(newSection);

		// 첫 정류장 추가 or 마지막 정류장 추가
		if (newSection.getUpStation().equals(getFinalStation())
			|| newSection.getDownStation().equals(getFirstStation())
		) {
			sectionList.add(newSection);
			return;
		}

		addMiddleSection(newSection);
	}

	private void stationIsCanNotBeTheSame(Section newSection) {
		if (newSection.getUpStation().equals(newSection.getDownStation())) {
			throw new IllegalArgumentException("상행역과 하행역은 동일할 수 없습니다.");
		}
	}

	private void checkExistingStation(Section newSection) {
		Station firstStation = getFirstStation();

		if (newSection.getDownStation().equals(firstStation)) {
			checkExistingStation(newSection.getUpStation());
			return;
		}

		checkExistingStation(newSection.getDownStation());
	}

	private void addMiddleSection(Section newSection) {
		Predicate<Section> sectionByUpStationPredicate = findSectionByUpStationPredicate(newSection.getUpStation());
		Section changeSection = findSectionByStation(sectionByUpStationPredicate);

		Integer distance = changeSection.getDistance() - newSection.getDistance();
		if (distance.compareTo(0) <= 0) {
			throw new IllegalArgumentException("추가 하려는 구간의 길이는 기존 구간의 길이 보다 크거나 같을 수 없습니다.");
		}

		Section changeNewSection = new Section(
			changeSection.getLine(),
			newSection.getDownStation(),
			changeSection.getDownStation(),
			distance
		);

		sectionList.remove(changeSection);
		sectionList.add(newSection);
		sectionList.add(changeNewSection);
	}

	private void checkExistingStation(Station station) {
		List<Station> existsStation = getStations();

		if (existsStation.contains(station)) {
			throw new IllegalArgumentException("추가 할려는 정류장은 이미 해당 노선에 존재하는 정류장입니다.");
		}
	}

	private void checkFinalStation(Station station) {
		if (sectionList.isEmpty()) {
			throw new IllegalArgumentException("해당 노선은 설정된 구간이 없습니다.");
		}

		if (!getFinalStation().getId().equals(station.getId())) {
			throw new IllegalArgumentException("해당 노선의 마지막 정류장이 아닙니다.");
		}
	}

	public Station getFirstStation() {
		if (sectionList.isEmpty()) {
			return new Station("");
		}

		return sectionList.get(0).getUpStation();
	}

	public Station getFinalStation() {
		if (sectionList.isEmpty()) {
			return new Station("");
		}

		List<Long> upStationIds = sectionList.stream()
			.map(section -> section.getUpStation().getId())
			.collect(toList());

		return sectionList.stream()
			.filter(isFinalStation(upStationIds))
			.findFirst()
			.orElseThrow(EntityNotFoundException::new)
			.getDownStation();
	}

	private Predicate<Section> isFinalStation(List<Long> upStationIds) {
		return section -> !upStationIds.contains(section.getDownStation().getId());
	}

	public void remove(Station deleteTargetStation) {
		checkOnlyTwoStations();
		Predicate<Section> sectionByUpStationPredicate = findSectionByUpStationPredicate(deleteTargetStation);
		Predicate<Section> sectionByDownStationPredicate = findSectionByDownStationPredicate(deleteTargetStation);

		Section deleteStation = findSectionByStation(sectionByUpStationPredicate);
		Section changeStation = findSectionByStation(sectionByDownStationPredicate);

		Section newSection =
			new Section(
				changeStation.getLine(),
				changeStation.getUpStation(),
				deleteStation.getDownStation(),
				deleteStation.getDistance() + changeStation.getDistance()
			);

		sectionList.remove(deleteStation);
		sectionList.remove(changeStation);
		sectionList.add(newSection);
	}

	private Section findSectionByStation(Predicate<Section> predicate) {
		return sectionList.stream()
			.filter(predicate)
			.findFirst()
			.orElseThrow(EntityNotFoundException::new);
	}

	private static Predicate<Section> findSectionByUpStationPredicate(Station deleteTargetStation) {
		return section -> section.getUpStation().equals(deleteTargetStation);
	}

	private static Predicate<Section> findSectionByDownStationPredicate(Station deleteTargetStation) {
		return section -> section.getDownStation().equals(deleteTargetStation);
	}

	private void checkOnlyTwoStations() {
		if (sectionList.size() < 2) {
			throw new IllegalArgumentException("해당 노선은 두개의 정류장만 존재 하므로, 삭제할 수 없습니다.");
		}
	}

	public Section getSectionMatchesDownStation(Station station) {
		return sectionList.stream()
			.filter(section -> section.getDownStation().getId().equals(station.getId()))
			.findFirst()
			.orElseThrow(() -> new EntityNotFoundException(""));
	}

	public List<Station> getSortedStations() {
		List<Station> stations = sectionList.stream()
			.sorted()
			.map(Section::getUpStation)
			.collect(toList());
		stations.add(getFinalStation());
		return stations;
	}

	public List<Station> getStations() {
		return sectionList.stream()
			.flatMap(section -> section.getStations().stream())
			.distinct()
			.collect(toList());
	}
}
