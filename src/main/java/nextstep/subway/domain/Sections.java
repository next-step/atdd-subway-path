package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private final List<Section> sections = new ArrayList<>();

	public void addSection(Line line, Station upStation, Station downStation, int distance) {
		this.sections.add(new Section(line, upStation, downStation, distance));
	}

	public List<Section> getList() {
		return Collections.unmodifiableList(this.sections);
	}
}
