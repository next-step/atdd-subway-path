package nextstep.subway.domain;

import org.jgrapht.GraphPath;

import java.util.List;

public class Path {

    private final GraphPath graphPath;

    Path(GraphPath graphPath) {
        this.graphPath = graphPath;
    }

    public List<Station> getVertexList() {
        return this.graphPath.getVertexList();
    }

    public Integer getWeight() {
        return (int) this.graphPath.getWeight();
    }
}
