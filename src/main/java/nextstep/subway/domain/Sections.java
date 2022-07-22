package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
		Set<Station> stationSet = new LinkedHashSet<>();
		for(Section section : sectionList){
			stationSet.add(section.getUpStation());
			stationSet.add(section.getDownStation());
		}
		List<Station> stationList = new ArrayList<>();
		stationList.addAll(stationSet);
		return stationList;
	}

	public void remove(Section section) {
		this.sectionList.remove(section);
	}

	public Section getLastSection(){
		return sectionList.get(sectionList.size() - 1);
	}
}
