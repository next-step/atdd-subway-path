package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

public class Sections {
	@OneToMany(mappedBy = "line",
		cascade = {CascadeType.PERSIST, CascadeType.MERGE},
		orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public Sections(List<Section> sections) {
		this.sections = sections;
	}

	public List<Section> getSections() {
		return sections;//Collections.unmodifiableList(sections);
	}

	public List<Station> getStations() {
		List<Station> stations = new ArrayList<>();
		sections.forEach(station -> stations.addAll(station.getAllStations()));
		return stations;
	}

	public void add(Section addSection) {
		this.sections.add(addSection);
	}

	public void remove(Station station) {
		sections.removeIf(section -> section.isRemovable(station.getId()));
	}
}
