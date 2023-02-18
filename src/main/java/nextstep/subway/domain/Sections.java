package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	protected Sections() {
	}

	public List<Section> getSections() {
		return sections;
	}

	public void addSection(Section section) {
		this.sections.add(section);
	}

	public List<Station> getStations() {
		List<Station> stations = sections.stream().map(Section::getUpStation).collect(Collectors.toList());

		stations.add(sections.get(getLastIndexOfSections()).getDownStation());

		return stations;
	}

	private int getLastIndexOfSections() {
		return sections.size() - 1;
	}

	public boolean isLastDownStation(Station station) {
		return sections.get(getLastIndexOfSections()).getDownStation().equals(station);
	}

	public void removeLastSection() {
		sections.remove(getLastIndexOfSections());
	}
}
