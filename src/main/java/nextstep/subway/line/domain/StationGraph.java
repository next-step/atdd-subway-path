package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;
import java.util.stream.Collectors;

public class StationGraph {

    private GraphPath<Station, DefaultWeightedEdge> graphPath;

    public StationGraph(GraphPath<Station, DefaultWeightedEdge> graphPath) {
        this.graphPath = graphPath;
    }

    public List<StationResponse> getStations() {
        List<Station> shortestPath = graphPath.getVertexList();

        return shortestPath.stream()
                .map(s -> StationResponse.of(s))
                .collect(Collectors.toList());
    }

    public int getIntegerWeight() {
        return (int) graphPath.getWeight();
    }
}
