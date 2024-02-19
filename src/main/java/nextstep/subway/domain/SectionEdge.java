package nextstep.subway.domain;

import org.jgrapht.graph.DefaultEdge;

public class SectionEdge extends DefaultEdge {

    private final Long source;
    private final Long target;
    private final int weight;

    public SectionEdge(Long source, Long target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public Long getSource() {
        return source;
    }

    public Long getTarget() {
        return target;
    }

    public int getWeight() {
        return weight;
    }

}
