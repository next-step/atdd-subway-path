package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.ShortestPath;
import nextstep.subway.exception.CustomException;
import nextstep.subway.exception.code.CommonCode;
import nextstep.subway.exception.code.PathCode;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Optional;

public class SubwayMap {

    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    public SubwayMap(final List<Line> lines) {
        dijkstraShortestPath = new DijkstraShortestPath<>(createWeightedGragh(lines));
    }

    public ShortestPath getShortestPath(final Station source, final Station target) {
        checkSameStation(source, target);

        GraphPath<Station, DefaultWeightedEdge> shortestPath = Optional.ofNullable(dijkstraShortestPath.getPath(source, target))
                                                                       .orElseThrow(() -> new CustomException(PathCode.NOT_LINKED));

        return new ShortestPath((int) shortestPath.getWeight(), shortestPath.getVertexList());
    }

    private void checkSameStation(final Station source, final Station target) {
        if (source.equals(target)) {
            throw new CustomException(CommonCode.PARAM_INVALID);
        }
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
