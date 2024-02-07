package subway.line;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import subway.station.Station;

@Entity
public class Line {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 20, nullable = false)
	private String name;

	@Column(length = 20, nullable = false)
	private String color;

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private final List<Section> sections = new ArrayList<>();

	protected Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public List<Section> getSections() {
		return sections;
	}

	public void changeName(String name) {
		this.name = name;
	}

	public void changeColor(String color) {
		this.color = color;
	}

	public void addSection(Station upStation, Station downStation, Integer distance) {
		checkExistingStation(upStation);
		checkFinalStation(upStation);
		Section section = new Section(this, upStation, downStation, distance);
		this.sections.add(section);
	}

	private void checkExistingStation(Station upStation) {
		List<Long> existsStationIds = sections
			.stream()
			.map(section -> section.getUpStation().getId())
			.collect(toList());

		if (existsStationIds.contains(upStation.getId())) {
			throw new IllegalArgumentException("추가 할려는 정류장은 이미 해당 노선에 존재하는 정류장입니다.");
		}
	}

	private void checkFinalStation(Station station) {
		if (sections.isEmpty()) {
			return;
		}

		if (!getFinalStation().getId().equals(station.getId())) {
			throw new IllegalArgumentException("해당 노선의 마지막 정류장이 아닙니다.");
		}
	}

	private Station getFinalStation() {
		if (sections.isEmpty()) {
			return new Station("");
		}

		List<Long> upStationIds = sections.stream()
			.map(section -> section.getUpStation().getId())
			.collect(toList());

		return sections.stream()
			.filter(isFinalStation(upStationIds))
			.findFirst()
			.orElseThrow(EntityNotFoundException::new)
			.getDownStation();
	}

	private Predicate<Section> isFinalStation(List<Long> upStationIds) {
		return section -> !upStationIds.contains(section.getDownStation().getId());
	}

	public List<Station> getSortedStations() {
		Station finalStation = getFinalStation();
		List<Station> stations = sections.stream()
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

	public void removeStation(Station station) {
		checkDeletableStation(station);

		Section section = getSectionMatchesDownStation(station);

		sections.remove(section);
	}

	private void checkDeletableStation(Station station) {
		checkOnlyTwoStations();
		checkFinalStation(station);
	}

	private void checkOnlyTwoStations() {
		if (sections.size() < 2) {
			throw new IllegalArgumentException("해당 노선은 두개의 정류장만 존재 하므로, 삭제할 수 없습니다.");
		}
	}

	private Section getSectionMatchesDownStation(Station station) {
		return sections.stream()
			.filter(section -> section.getDownStation().getId().equals(station.getId()))
			.findFirst()
			.orElseThrow(() -> new EntityNotFoundException(""));
	}
}
