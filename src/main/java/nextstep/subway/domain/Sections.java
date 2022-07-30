package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private final List<Section> sections = new ArrayList<>();

	public void addToLast(Section section) {
		this.sections.add(section);
	}

	public void addById(int index, Section section) {
		this.sections.add(index, section);
	}

	public void add(Section section) {
		SectionAddPosition sectionPosition = SectionAddPosition.from(this, section);
		sectionPosition.add(this, section);
	}

	public List<Station> getStations() {
		return getStations(this.sections);
	}

	public List<Station> getStations(List<Section> sections) {
		List<Station> stations = sections.stream().map(Section::getDownStation).collect(Collectors.toList());
		stations.add(0, sections.get(0).getUpStation());
		return stations;
	}

	public List<Station> getOrderedStations() {
		Sections orderedSections = new Sections();
		SectionAddPosition sectionPosition;

		for(Section section: sections) {
			sectionPosition = SectionAddPosition.from(orderedSections, section);
			sectionPosition.add(orderedSections, section);
		}

		return getStations(orderedSections.getSections());
	}

	public void removeById(int index) {
		this.sections.remove(index);
	}

	public void remove(Station station) {
		SectionRemovePosition sectionPosition = SectionRemovePosition.from(this, station);
		sectionPosition.remove(this, station);
	}

	public boolean isEmpty() {
		return this.sections.isEmpty();
	}

	public Station firstStation() {
		return this.sections.get(0).getUpStation();
	}

	public Station lastStation() {
		return this.sections.get(this.sections.size() - 1).getDownStation();
	}

	public Station getDownStationById(int index) {
		return this.sections.get(index).getDownStation();
	}

	public Station getUpStationById(int index) {
		return this.sections.get(index).getUpStation();
	}

	public int getDistanceById(int index) {
		return this.sections.get(index).getDistance();
	}

	public int size() {
		return this.sections.size();
	}

	public Section getSectionById(int index) {
		return this.sections.get(index);
	}

	public boolean contains(Station station) {
		return this.getStations().contains(station);
	}
}
