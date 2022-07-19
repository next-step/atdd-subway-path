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
	private List<Section> sectionList = new ArrayList<>();

	public List<Section> getSectionList() {
		return this.sectionList;
	}

	public void addSection(Section section) {
		this.sectionList.add(section);
	}

	public List<Station> getStations() {
		return this.sectionList.stream()
			.map(Section::getStationList)
			.flatMap(List::stream)
			.distinct()
			.collect(Collectors.toList());
	}

	public void remove(Section section) {
		this.sectionList.remove(section);
	}
}
