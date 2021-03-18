package nextstep.subway.line.domain;

import nextstep.subway.line.exception.NotExistPathInfoException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StationGraph {

    private GraphPath<Station, DefaultWeightedEdge> graphPath;

    public StationGraph(GraphPath<Station, DefaultWeightedEdge> graphPath) {
        validateGraph(graphPath);
        this.graphPath = graphPath;
    }

    private void validateGraph(GraphPath<Station, DefaultWeightedEdge> graphPath) {
        if(Objects.isNull(graphPath)) {
            throw new NotExistPathInfoException();
        }
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
