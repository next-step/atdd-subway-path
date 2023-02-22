package nextstep.subway.domain;

import lombok.Getter;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;

@Getter
public class ShortestPath {
    private final List<Station> stations;
    private final double totalDistance;

    private ShortestPath(final List<Station> stations, final double totalDistance) {
        this.stations = stations;
        this.totalDistance = totalDistance;
    }

    public static ShortestPath from(final GraphPath<Station, DefaultWeightedEdge> graphPath) {
        return new ShortestPath(
                graphPath.getVertexList(),
                graphPath.getWeight()
        );
    }
}
