package nextstep.subway.domain;

import nextstep.subway.enums.SubwayErrorMessage;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;
import java.util.Objects;

public class StationDijkstraShortestPath {

    private DijkstraShortestPath dijkstraShortestPath;
    private Station source;
    private Station target;

    public StationDijkstraShortestPath(StationWeightedMultigraph graph, Station source, Station target) {
        this.source = source;
        this.target = target;
        this.dijkstraShortestPath = new DijkstraShortestPath(graph.getGraph());
    }

    public List<Station> getVertexList() {
        validPath();
        return getPath().getVertexList();
    }

    public int getWeight() {
        validPath();
        return (int) getPath().getWeight();
    }

    private void validPath() {
        if(isUnconnected()) {
            throw new IllegalArgumentException(SubwayErrorMessage.UNCONNECTED_PATH.getMessage());
        }
        if (isSameSourceAndDestination()) {
            throw new IllegalArgumentException(SubwayErrorMessage.SAME_SOURCE_AND_DESTINATION.getMessage());
        }
    }

    private boolean isSameSourceAndDestination() {
        return source.equals(target);
    }

    private GraphPath getPath() {
        return dijkstraShortestPath.getPath(source, target);
    }

    private boolean isUnconnected() {
        return Objects.isNull(getPath());
    }


}
