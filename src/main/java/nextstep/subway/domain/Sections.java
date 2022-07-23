package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

		validateExisingStation(newSection);

		Optional<Section> sameUpStationSection = getSameUpStationSection(newSection);

		if(sameUpStationSection.isPresent()){
			validateSectionDistance(newSection, sameUpStationSection.get());
			addSectionUpStationBased(newSection, sameUpStationSection.get());
			return;
		}

		Optional<Section> sameDownStationSection = getSameDownStationSection(newSection);

		if(sameDownStationSection.isPresent()){
			validateSectionDistance(newSection, sameDownStationSection.get());
			addSectionDownStationBased(newSection, sameDownStationSection.get());
			return;
		}

		if(isSameNewSectionDownStationAndUpStation(newSection)){
			sectionList.add(newSection);
			return;
		}

		if(isSameNewSectionUpStationAndDownStation(newSection)){
			sectionList.add(newSection);
			return;
		}

		throw new IllegalArgumentException("구간추가가 불가합니다.");
	}

	private void validateExisingStation(Section newSection) {
		if(findSameUpAndDownStation(newSection)){
			throw new IllegalArgumentException("상행역과 하행역이 모두 등록되어있습니다.");
		}
	}

	private void addSectionDownStationBased(Section newSection, Section section) {
		sectionList.remove(section);
		sectionList.add(new Section(section.getLine(), section.getUpStation(), newSection.getUpStation(),
			section.getDistance() - newSection.getDistance()));
		sectionList.add(new Section(section.getLine(), newSection.getUpStation(), newSection.getDownStation(),
			newSection.getDistance()));
	}

	private Optional<Section> getSameDownStationSection(Section newSection) {
		return this.sectionList.stream()
			.filter(section -> section.isSameDownStation(newSection))
			.findFirst();
	}

	private void validateSectionDistance(Section newSection, Section section) {
		if(newSection.isLongerDistance(section)){
			throw new IllegalArgumentException("추가하려는 구간의 길이는 기존 구간의 길이와 같거나 길수 없습니다.");
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
			.filter(section -> section.isSameUpStation(newSection))
			.findFirst();
	}

	public List<Station> getStations() {
		if(sectionList.isEmpty()){
			return Collections.emptyList();
		}

		List<Station> sortedStationList = new ArrayList<>();
		Section section = getFirstSection();
		Section lastSection = getLastSection();

		sortedStationList.add(section.getUpStation());

		while(!section.equals(lastSection)){
			sortedStationList.add(section.getDownStation());
			section = nextSection(section);
		}

		sortedStationList.add(lastSection.getDownStation());
		return sortedStationList;
	}

	private Section nextSection(Section currentSection) {
		return this.sectionList.stream()
			.filter(section -> currentSection.getDownStation().equals(section.getUpStation()))
			.findAny()
			.orElseThrow();
	}

	public void remove(Section section) {
		this.sectionList.remove(section);
	}

	public Section getLastSection(){
		return this.sectionList.stream()
			.filter(section -> findSameUpStationSection(section.getDownStation()))
			.findAny()
			.orElseThrow();
	}

	private boolean findSameUpStationSection(Station downStation) {
		return this.sectionList.stream()
			.noneMatch(section -> section.getUpStation().equals(downStation));
	}

	public boolean isSameNewSectionDownStationAndUpStation(Section newSection) {
		Section firstSection = getFirstSection();
		return firstSection.getUpStation().equals(newSection.getDownStation());
	}

	public Section getFirstSection() {
		return this.sectionList.stream()
			.filter(section -> findSameDownStationSection(section.getUpStation()))
			.findAny()
			.orElseThrow();
	}

	private boolean findSameDownStationSection(Station upStation){
		return this.sectionList.stream()
			.noneMatch(section -> section.getDownStation().equals(upStation));
	}

	public boolean isSameNewSectionUpStationAndDownStation(Section newSection) {
		Section lastSection = getLastSection();
		return lastSection.getDownStation().equals(newSection.getUpStation());
	}

	private boolean findSameUpAndDownStation(Section newSection){
		return !this.sectionList.stream().noneMatch(section -> section.getUpStation().equals(newSection.getUpStation())) &&
			!this.sectionList.stream().noneMatch(section -> section.getDownStation().equals(newSection.getDownStation()));
	}
}
