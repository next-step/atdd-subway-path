package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class PathFinder {
    public List<Station> findPath(List<Line> lines, Station source, Station target) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(buildGraph(lines));
        validateGraph(source, target, dijkstraShortestPath);

        return dijkstraShortestPath.getPath(source, target).getVertexList();
    }

    public int findPathWeight(List<Line> lines, Station source, Station target) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(buildGraph(lines));
        validateGraph(source, target, dijkstraShortestPath);

        return (int) dijkstraShortestPath.getPathWeight(source, target);
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> buildGraph(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        graphAddVertex(lines, graph);
        graphAddEdge(lines, graph);

        return graph;
    }

    private void graphAddEdge(List<Line> lines, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lines.stream()
                .flatMap(line -> line.getLineSections().stream())
                .forEach(section -> graph.setEdgeWeight(
                        graph.addEdge(section.getUpStation(), section.getDownStation())
                        , section.getDistance()));
    }

    private void graphAddVertex(List<Line> lines, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lines.stream()
                .flatMap(line -> line.getStations().stream())
                .distinct()
                .forEach(station -> graph.addVertex(station));
    }

    private void validateGraph(Station source, Station target, DijkstraShortestPath dijkstraShortestPath) {
        if (Objects.isNull(dijkstraShortestPath.getPath(source, target))) {
            throw new DataIntegrityViolationException("출발역과 도착역이 연결되어 있지 않습니다.");
        }
    }
}
