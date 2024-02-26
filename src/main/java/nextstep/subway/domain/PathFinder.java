package nextstep.subway.domain;

import nextstep.subway.exception.PathSourceTargetNotConnectedException;
import nextstep.subway.exception.PathSourceTargetSameException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;


public class PathFinder {
    public Path findPath(List<Line> lineList, Station source, Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = createGraph(lineList);

        validateSourceAndTargetAreDifferent(source, target);

        DijkstraShortestPath<Station, DefaultWeightedEdge> shortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> path = shortestPath.getPath(source, target);

        validatePathExists(path);

        return Path.builder().path(path.getVertexList()).distance(shortestPath.getPathWeight(source, target)).build();
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createGraph(List<Line> lineList) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        lineList.stream()
                .flatMap(line -> line.getSections().getSections().stream())
                .distinct()
                .forEach(section -> {
                    graph.addVertex(section.getUpStation());
                    graph.addVertex(section.getDownStation());
                    graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
                });

        return graph;
    }

    private void validateSourceAndTargetAreDifferent(Station source, Station target) {
        if (source.equals(target)) {
            throw new PathSourceTargetSameException("출발역과 도착역이 같으면 경로를 조회할 수 없습니다.");
        }
    }

    private void validatePathExists(GraphPath<Station, DefaultWeightedEdge> path) {
        if (path == null) {
            throw new PathSourceTargetNotConnectedException("출발역과 도착역이 연결되어 있지 않습니다.");
        }
    }
}
