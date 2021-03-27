package nextstep.subway.path.domain;

import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;

public class StationGraphPath {

    private final GraphPath<Station, DefaultWeightedEdge> graph;

    private StationGraphPath(GraphPath<Station, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }

    public static StationGraphPath of(GraphPath<Station, DefaultWeightedEdge> graph) {
        if (graph == null) {
            throw new RuntimeException("연결된 경로를 찾을 수 없습니다.");
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
