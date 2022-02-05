package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public Sections() {

	}

	public void add(Section section) {
		if (this.sections.isEmpty()){
			this.sections.add(section);
			return;
		}

		// TODO : 상행구간 재배치
		this.sections.stream()
			.filter(it -> it.isEqualDownStation(section.getDownStation()))
			.findFirst()
			.ifPresent(it -> {
				sections.add(new Section(section.getLine(), it.getUpStation(), section.getUpStation(),
					it.getDistance() - section.getDistance()));
				sections.remove(it);
			});

		// TODO : 하행구간 재배치
		this.sections.stream()
			.filter(it -> it.isEqualUpStation(section.getUpStation()))
			.findFirst()
			.ifPresent(it -> {
				sections.add(new Section(section.getLine(), section.getDownStation(), it.getDownStation(),
					it.getDistance() - section.getDistance()));
				sections.remove(it);
			});

		this.sections.add(section);
	}

	public List<Station> findAllStations() {
		// TODO : 모든 역 찾기
		//  첫번째 상행역을 찾고, 그 뒤로 구간에 연결되는 하행역을 찾는다
		List<Station> stations = new ArrayList<>();
		Station upStation = findFirstUpStation();
		stations.add(upStation);

		while (true) {
			Station station = upStation;
			Optional<Section> section = findSectionByUpStation(station);
			if (!section.isPresent()) {
				break;
			}
			// 다음 상행역은 구간에서 찾은 하행역
			upStation = section.get().getDownStation();
			stations.add(upStation);
		}

		return stations;
	}

	private Station findFirstUpStation() {
		// TODO : 첫번째 역 구하기
		//  상행역 중 하행역이 없는게 첫번째 역
		List<Station> upStations = this.sections.stream()
			.map(Section::getUpStation)
			.collect(Collectors.toList());

		List<Station> downStations = this.sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());

		return upStations.stream()
			.filter(station -> !downStations.contains(station))
			.findFirst()
			.orElseThrow(IllegalArgumentException::new);
	}

	private Optional<Section> findSectionByUpStation(Station station) {
		return this.sections.stream()
			.filter(section -> section.isEqualUpStation(station))
			.findFirst();
	}

	public int size() {
		return this.sections.size();
	}

	public List<Section> findAll() {
		return this.sections;
	}

	public Section findByIndex(int index) {
		return this.sections.get(index);
	}

	public void removeByIndex(int index) {
		this.sections.remove(index);
	}
}
