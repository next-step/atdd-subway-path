package nextstep.subway.path;

import lombok.RequiredArgsConstructor;
import nextstep.subway.exception.PathNotFoundException;
import nextstep.subway.line.LineRepository;
import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;
import nextstep.subway.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PathFinder {
    private final LineRepository lineRepository;

    private WeightedMultigraph<String, DefaultWeightedEdge> init() {
        WeightedMultigraph<String, DefaultWeightedEdge> graph
                = new WeightedMultigraph(DefaultWeightedEdge.class);
        lineRepository.findAll().forEach(line -> {
            line.getSections().forEach(section -> {
                String upStationName = Long.toString(section.getUpstation().getId());
                String downStationName = Long.toString(section.getDownstation().getId());

                graph.addVertex(upStationName);
                graph.addVertex(downStationName);

                DefaultWeightedEdge edge = graph.addEdge(upStationName, downStationName);

                if (edge != null) {
                    graph.setEdgeWeight(edge, section.getDistance());
                }

            });
        });

        return graph;
    }

    public Pair<List<String>, Integer> findShortestPath(String sourceId, String targetId) {
        WeightedMultigraph<String, DefaultWeightedEdge> graph = init();
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
