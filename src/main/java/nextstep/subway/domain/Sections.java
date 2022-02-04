package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public void add(Section section) {
		this.sections.add(section);
	}

	public boolean isEmpty() {
		return this.sections.isEmpty();
	}

	public int size() {
		return this.sections.size();
	}

	public List<Section> allSections() {
		return this.sections;
	}

	public Section sectionByIndex(int index) {
		return this.sections.get(index);
	}

	public void removeByIndex(int index) {
		this.sections.remove(index);
	}
}
