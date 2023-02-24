package nextstep.subway.domain;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.util.Assert;

public class SectionEdge extends DefaultWeightedEdge {
    private Section section;

    public SectionEdge(Section section) {
        Assert.notNull(section, "section must not be null");
        this.section = section;
    }

    public int distance() {
        return section.getDistance();
    }
}
