package nextstep.subway.path;

import nextstep.subway.exception.PathNotFoundException;
import nextstep.subway.section.Section;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PathFinder {
    WeightedMultigraph<String, DefaultWeightedEdge> graph
            = new WeightedMultigraph(DefaultWeightedEdge.class);
    public void apply(boolean isAdd, Section section) {
        String upStationId = Long.toString(section.getUpstation().getId());
        String downStationId = Long.toString(section.getDownstation().getId());

        if (isAdd) {
            graph.addVertex(upStationId);
            graph.addVertex(downStationId);

            DefaultWeightedEdge edge = graph.addEdge(upStationId, downStationId);

            if (edge != null) {
                graph.setEdgeWeight(edge, section.getDistance());
            }
        } else {
            DefaultWeightedEdge edge = graph.getEdge(upStationId, downStationId);

            if (edge != null) {
                graph.removeEdge(edge);
            }
        }
    }

    public Pair<List<String>, Integer> findShortestPath(String sourceId, String targetId) {
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<String, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(sourceId, targetId);

        if (path == null) {
            throw new PathNotFoundException("가능한 경로가 존재하지 않습니다.");
        }

        List<String> shortestPath = path.getVertexList();
        int totalDistance = (int) path.getEdgeList().stream()
                .mapToDouble(edge -> graph.getEdgeWeight(edge))
                .sum();

        return Pair.of(shortestPath, totalDistance);
    }
}
