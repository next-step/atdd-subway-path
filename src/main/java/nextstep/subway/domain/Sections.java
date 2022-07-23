package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private List<Section> sectionList = new ArrayList<>();

	protected Sections(){

	}

	private Sections(List<Section> sectionList) {
		this.sectionList = sectionList;
	}

	public static Sections from(List<Section> sectionList){
		return new Sections(sectionList);
	}

	public List<Section> getSectionList() {
		return this.sectionList;
	}

	public void addSection(Section newSection) {
		if(sectionList.isEmpty()){
			sectionList.add(newSection);
			return;
		}

		Optional<Section> sameUpStationSection = getSameUpStationSection(newSection);

		if(sameUpStationSection.isPresent()){
			addSectionUpStationBased(newSection, sameUpStationSection.get());
			return;
		}

	}

	private void addSectionUpStationBased(Section newSection, Section section) {
		sectionList.remove(section);
		sectionList.add(new Section(section.getLine(), section.getUpStation(), newSection.getDownStation(),
			newSection.getDistance()));
		sectionList.add(new Section(section.getLine(), newSection.getDownStation(), section.getDownStation(),
			section.getDistance() - newSection.getDistance()));
	}

	public Optional<Section> getSameUpStationSection(Section newSection){
		return this.sectionList.stream()
			.filter(section -> section.getUpStation().getName().equals(newSection.getUpStation().getName()))
			.findFirst();
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
