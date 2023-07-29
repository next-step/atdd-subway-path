package nextstep.subway.domain;

import org.jgrapht.graph.DefaultWeightedEdge;

public class SectionEdge extends DefaultWeightedEdge {

	private final Section section;

	public SectionEdge(Section section) {
		this.section = section;
	}

	public Section getSection() {
		return section;
	}
}
