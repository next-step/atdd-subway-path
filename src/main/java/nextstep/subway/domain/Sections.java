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

		updateUpSection(section);
		updateDownSection(section);

		this.sections.add(section);
	}

	private void updateDownSection(Section section) {
		// ex)
		// 기존 : A - B
		// 신규 : A - C
		// A 가 상행역으로 같음 => 기존구간을 삭제하고 하행역끼리 연결 (C - B)
		// 그 후 신규 구간 추가 (A - C)
		// => A - C, C - B 로 구간 생성
		this.sections.stream()
			.filter(it -> it.isEqualUpStation(section.getUpStation()))
			.findFirst()
			.ifPresent(it -> {
				sections.add(new Section(section.getLine(), section.getDownStation(), it.getDownStation(),
					it.getDistance() - section.getDistance()));
				sections.remove(it);
			});
	}

	private void updateUpSection(Section section) {
		// ex)
		// 기존 : A - B
		// 신규 : C - B
		// B 가 하행역으로 같음 => 기존구간을 삭제하고 상행역끼리 연결 (A - C)
		// 그 후 신규 구간 추가 (C - B)
		// => A - C, C - B 로 구간 생성
		this.sections.stream()
			.filter(it -> it.isEqualDownStation(section.getDownStation()))
			.findFirst()
			.ifPresent(it -> {
				sections.add(new Section(section.getLine(), it.getUpStation(), section.getUpStation(),
					it.getDistance() - section.getDistance()));
				sections.remove(it);
			});
	}

	public List<Station> findAllStations() {
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
