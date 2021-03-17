package nextstep.subway.path.domain;

import nextstep.subway.path.exception.DoseNotConnectedException;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class StationGraphPath {

    private final GraphPath<Station, DefaultWeightedEdge> graph;

    private StationGraphPath(GraphPath<Station, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }

    public static StationGraphPath of(GraphPath<Station, DefaultWeightedEdge> graph) {
        if (graph == null) {
            throw new DoseNotConnectedException();
        }
        return new StationGraphPath(graph);
    }

    public List<Station> getVertexStations() {
        return graph.getVertexList();
    }

    public int getDistance() {
        return (int) graph.getWeight();
    }

}
