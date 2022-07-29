package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private final List<Section> sections = new ArrayList<>();

	public void add(Section section) {
		SectionAddPosition sectionPosition = SectionAddPosition.from(this, section);
		sectionPosition.add(this.sections, section);
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
			sectionPosition.add(orderedSections.getSections(), section);
		}

		return getStations(orderedSections.getSections());
	}

	public void remove(Station station) {
		SectionRemovePosition sectionPosition = SectionRemovePosition.from(this, station);
		sectionPosition.remove(this.sections, station);

		// int count = this.sections.size();
		// if(count <= 1) {
		// 	throw new IllegalArgumentException("There is no station");
		// }
		//
		// Section lastSection = this.sections.get(count - 1);
		// if (!lastSection.getDownStation().equals(station)) {
		// 	throw new IllegalArgumentException("Station does not match");
		// }
		// this.sections.remove(lastSection);
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
}
