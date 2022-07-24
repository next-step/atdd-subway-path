package nextstep.subway.domain;

import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@Getter
public class Sections {

	private static final int FIRST_IDX = 0;

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	List<Section> sections = new ArrayList<>();

	public boolean isEmpty() {
		return sections.isEmpty();
	}

	public void add(Section section) {
		add(section.getLine(), section.getUpStation(), section.getDownStation(), section.getDistance());
	}

	public void add(Line line, Station upStation, Station downStation, int distance) {
		if (isStartWithUpStation(upStation)) {
			Section section = sections.stream()
					.filter(s -> s.getUpStation().equals(upStation))
					.findFirst()
					.orElseThrow(IllegalArgumentException::new);
			this.sections.remove(section);
			this.sections.add(new Section(line, downStation, section.getDownStation(), modifiedDistance(section.getDistance(), distance)));
			this.sections.add(new Section(line, upStation, downStation, distance));
			return;
		}

		this.sections.add(new Section(line, upStation, downStation, distance));
	}

	private boolean isStartWithUpStation(Station upStation) {
		return sections.stream().anyMatch(section -> section.getUpStation().equals(upStation));
	}

	private Integer modifiedDistance(Integer origin, Integer insert) {
		return origin - insert;
	}

	public List<Station> getStations() {

		if (isEmpty()) {
			return Collections.emptyList();
		}
		List<Station> stations = sections.stream()
				.map(Section::getDownStation)
				.collect(Collectors.toList());
		stations.add(FIRST_IDX, sections.get(FIRST_IDX).getUpStation());

		return stations;
	}

	public void removeSection(Station station) {
		if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
			throw new IllegalArgumentException();
		}

		sections.remove(this.getSections().size() - 1);
	}
}
