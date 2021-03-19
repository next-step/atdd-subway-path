package nextstep.subway.line.domain;

import nextstep.subway.line.exception.NotExistPathInfoException;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;
import java.util.Objects;

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

    public List<Station> getStations() {
        return graphPath.getVertexList();
    }

    public int getIntegerWeight() {
        return (int) graphPath.getWeight();
    }
}
