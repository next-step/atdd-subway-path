package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResult;
import nextstep.subway.common.ErrorMessage;
import nextstep.subway.domain.section.Section;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class DijkstraStationPathFind implements PathFind<Line, Station> {

    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;


    @Override
    public void init(List<Line> lines) {
        graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        lines.stream()
                .peek(line -> line.getStations().forEach(graph::addVertex))
                .map(Line::getSections)
                .forEach(sections -> sections.forEach(this::addEdge));
    }

    @Override
    public PathResult<Station> find(Station source, Station target) {
        validateEqualStation(source, target);
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath graphPath = dijkstraShortestPath.getPath(source, target);
        return createPathResponse(graphPath);
    }

    private void validateEqualStation(Station source, Station target) {
        if (Objects.equals(source, target)) {
            throw new IllegalArgumentException(ErrorMessage.DUPLICATED_PATH_FIND.toString());
        }
    }

    private PathResult<Station> createPathResponse(GraphPath<Station, DefaultWeightedEdge> graphPath) {
        return new PathResult<Station>(graphPath.getVertexList(), graphPath.getWeight());
    }
    private void addEdge(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        int distance = section.getDistance();
        graph.setEdgeWeight(graph.addEdge(upStation, downStation), distance);
    }
}
