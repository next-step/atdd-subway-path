package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.applicaion.dto.ShortestPathResult;
import nextstep.subway.exception.CustomException;
import nextstep.subway.exception.code.CommonCode;
import nextstep.subway.exception.code.PathCode;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PathFinder {
    public static ShortestPathResult calShortestPath(final List<Line> lines, final Station source, final Station target) {
        checkSameStation(source, target);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = createWeightedGragh(lines);
        GraphPath<Station, DefaultWeightedEdge> shortestPath = createShortestPath(source, target, graph);

        return new ShortestPathResult((int) shortestPath.getWeight(), shortestPath.getVertexList());
    }

    private static void checkSameStation(final Station source, final Station target) {
        if (source.equals(target)) {
            throw new CustomException(CommonCode.PARAM_INVALID);
        }
    }

    private static WeightedMultigraph<Station, DefaultWeightedEdge> createWeightedGragh(final List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        for (Line line : lines) {
            for (Section section : line.getSections().getSections()) {
                graph.addVertex(section.getUpStation());
                graph.addVertex(section.getDownStation());
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
            }
        }
        return graph;
    }

    private static GraphPath<Station, DefaultWeightedEdge> createShortestPath(final Station source,
                                                                              final Station target,
                                                                              final WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> shortestPath = dijkstraShortestPath.getPath(source, target);
        if (shortestPath == null) {
            throw new CustomException(PathCode.NOT_LINKED);
        }
        return shortestPath;
    }
}
