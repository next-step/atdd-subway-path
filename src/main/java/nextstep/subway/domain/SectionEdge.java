package nextstep.subway.domain;

import org.jgrapht.graph.DefaultEdge;

public class SectionEdge extends DefaultEdge {

    private final String source;
    private final String target;
    private final int weight;

    public SectionEdge(String source, String target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public int getWeight() {
        return weight;
    }

}
