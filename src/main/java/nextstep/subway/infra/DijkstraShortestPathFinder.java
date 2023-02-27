package nextstep.subway.infra;

import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.EntityNotFoundException;
import nextstep.subway.exception.InvalidInputException;
import nextstep.subway.exception.StationsNotConnectedException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class DijkstraShortestPathFinder implements PathFinder {
    final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    final DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);

    @Override
    public PathResponse find(List<Line> lines, Station source, Station target) {
        validatePathFinder(lines, source, target);

        lines.stream()
                .map(line -> line.getSections().getSections())
                .flatMap(Collection::stream)
                .forEach(section -> setGraph(section));

        GraphPath graphPath = dijkstraShortestPath.getPath(source, target);
        validateNotConnected(graphPath);

        List<Station> vertexList = graphPath.getVertexList();
        double distance = graphPath.getWeight();

        List<StationResponse> stations = vertexList.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());


        return new PathResponse(stations, distance);
    }

    private void setGraph(Section section) {
        graph.addVertex(section.getUpStation());
        graph.addVertex(section.getDownStation());
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
    }

    private void validatePathFinder(List<Line> lines, Station source, Station target) {
        if (Objects.equals(source, target)) {
            throw new InvalidInputException("The departure and arrival stations must not be the same.");
        }

        checkExistStationsInLines(lines, source, target);
    }

    private void validateNotConnected(GraphPath graphPath) {
        if (Objects.isNull(graphPath)) {
            throw new StationsNotConnectedException("Unconnected station.");
        }
    }

    private void checkExistStationsInLines(List<Line> lines, Station source, Station target) {
        boolean isContainSource = lines.stream().anyMatch(line -> line.isContainsStation(source));
        boolean isContainTarget = lines.stream().anyMatch(line -> line.isContainsStation(target));

        if (isContainSource && isContainTarget) {
            return;
        }

        throw new EntityNotFoundException("This station");
    }
}
