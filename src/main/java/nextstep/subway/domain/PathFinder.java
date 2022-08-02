package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.ShortestPathResult;
import nextstep.subway.exception.CustomException;
import nextstep.subway.exception.code.CommonCode;
import nextstep.subway.exception.code.PathCode;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PathFinder {
    public ShortestPathResult calShortestPath(final List<Line> lines, final Station source, final Station target) {
        checkSameStation(source, target);

        GraphPath<Station, DefaultWeightedEdge> shortestPath = createShortestPath(source, target, lines);

        return new ShortestPathResult((int) shortestPath.getWeight(), shortestPath.getVertexList());
    }

    private void checkSameStation(final Station source, final Station target) {
        if (source.equals(target)) {
            throw new CustomException(CommonCode.PARAM_INVALID);
        }
    }

    private GraphPath<Station, DefaultWeightedEdge> createShortestPath(final Station source,
                                                                       final Station target,
                                                                       final List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = createWeightedGragh(lines);
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> shortestPath = dijkstraShortestPath.getPath(source, target);
        if (shortestPath == null) {
            throw new CustomException(PathCode.NOT_LINKED);
        }
        return shortestPath;
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createWeightedGragh(final List<Line> lines) {
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
}
