package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private final List<Section> sections = new ArrayList<>();

	public void add(Section section) {
		this.sections.add(section);
	}

	public List<Station> getStations() {
		List<Station> stations = this.sections.stream().map(Section::getDownStation).collect(Collectors.toList());
		stations.add(0, this.sections.get(0).getUpStation());
		return stations;
	}

	public void remove(Station station) {
		int count = this.sections.size();
		if(count <= 1) {
			throw new IllegalArgumentException();
		}

		Section lastSection = this.sections.get(count - 1);
		if (!lastSection.getDownStation().equals(station)) {
			throw new IllegalArgumentException();
		}
		this.sections.remove(lastSection);
	}

	public Boolean isEmpty() {
		return this.sections.isEmpty();
	}
}
