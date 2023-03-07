package nextstep.subway.domain;

import nextstep.subway.exception.SectionBadRequestException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public void add(Section section) {
		sections.add(section);
	}

	public List<Section> getSections() {
		return sections;
	}

	public List<Station> getStations() {
		return sections.stream()
		        .map(Section::getStations)
		        .flatMap(Collection::stream)
		        .distinct()
		        .collect(Collectors.toList());
	}

	public boolean isEmpty() {
		return sections.isEmpty();
	}

	public boolean hasEnoughSize() {
		return sections.size() < 2;
	}

	public void remove(Station station) {
		if (isEmpty()) {
			throw new SectionBadRequestException("구간이 존재하지 않습니다.");
		}
		if (hasEnoughSize()) {
			throw new SectionBadRequestException("현재 노선은 구간이 1개 입니다.");
		}
		if (isNotEndStation(station)) {
			throw new SectionBadRequestException("노선에 등록된 하행 종점역만 제거 할 수 있습니다.");
		}

		int end = sections.size() - 1;
        sections.remove(end);
	}

	public boolean isNotEndStation(Station station) {
		int end = sections.size() - 1;
		Station endStation = sections.get(end).getDownStation();
		return !station.equals(endStation);
	}
}
