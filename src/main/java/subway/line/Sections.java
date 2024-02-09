package subway.line;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Comparator;
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

	public void add(Line line, Station upStation, Station downStation, Integer distance) {
		checkExistingStation(upStation);
		checkFinalStation(upStation);
		sectionList.add(new Section(line, upStation, downStation, distance));
	}

	private void checkExistingStation(Station upStation) {
		List<Long> existsStationIds = sectionList
			.stream()
			.map(section -> section.getUpStation().getId())
			.collect(toList());

		if (existsStationIds.contains(upStation.getId())) {
			throw new IllegalArgumentException("추가 할려는 정류장은 이미 해당 노선에 존재하는 정류장입니다.");
		}
	}

	private void checkFinalStation(Station station) {
		if (sectionList.isEmpty()) {
			return;
		}

		if (!getFinalStation().getId().equals(station.getId())) {
			throw new IllegalArgumentException("해당 노선의 마지막 정류장이 아닙니다.");
		}
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

	public void remove(Station finalStation) {
		checkDeletableStation(finalStation);
		Section section = getSectionMatchesDownStation(finalStation);
		sectionList.remove(section);
	}

	private void checkDeletableStation(Station station) {
		checkOnlyTwoStations();
		checkFinalStation(station);
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
		Station finalStation = getFinalStation();
		List<Station> stations = sectionList.stream()
			.sorted(stationComparator())
			.map(Section::getUpStation)
			.collect(toList());
		stations.add(finalStation);
		return stations;
	}

	private static Comparator<Section> stationComparator() {
		return (section, nextSection) -> {
			final int sort = 1;
			Long downStationId = section.getDownStation().getId();
			Long nextUpStationId = nextSection.getUpStation().getId();

			if (downStationId.equals(nextUpStationId)) {
				return -sort;
			}

			return sort;
		};
	}
}
