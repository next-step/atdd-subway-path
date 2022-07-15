package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

public class Sections {
	@OneToMany(mappedBy = "line",
		cascade = {CascadeType.PERSIST, CascadeType.MERGE},
		orphanRemoval = true)
	@OrderBy("id asc")
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

		if (!getLastSection().isSameWithDownStation(station.getId())) {
			throw new IllegalArgumentException();
		}

		sections.removeIf(section -> section.equals(getLastSection()));
	}

	private Section getLastSection() {
		return sections.get(sections.size() - 1);
		/*
		return sections.stream()
			.sorted(Comparator.comparing(Section::getId).reversed())
			.findFirst()
			.orElseThrow(IllegalArgumentException::new);

		 */
	}
}
