package nextstep.subway.applicaion;

import org.jgrapht.graph.DefaultWeightedEdge;

import lombok.Getter;
import nextstep.subway.domain.Section;

@Getter
public class SectionEdge extends DefaultWeightedEdge {

	private final Section section;

	public SectionEdge(Section section) {
		this.section = section;
	}

	@Override
	protected double getWeight() {
		return this.section.getDistance();
	}

	@Override
	protected Object getSource() {
		return this.section.getUpStation();
	}

	@Override
	protected Object getTarget() {
		return this.section.getDownStation();
	}
}
