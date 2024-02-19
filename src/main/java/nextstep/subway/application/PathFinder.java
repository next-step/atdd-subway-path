package nextstep.subway.application;

import nextstep.subway.application.dto.PathResult;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PathFinder {
    public PathResult calculateShortestPath(List<Line> lines, Station departureStation, Station arrivalStation) {
        GraphPath<Station, DefaultWeightedEdge> path = findShortestPath(
                departureStation,
                arrivalStation,
                buildGraphFromLines(lines, new WeightedMultigraph<Station, DefaultWeightedEdge>(DefaultWeightedEdge.class)));

        if (isPathNotFound(path)) {
            throw new IllegalArgumentException("출발역과 도착역이 연결되어 있지 않습니다.");
        }
        return generatePathResult(path);
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> buildGraphFromLines(List<Line> lines,
                                                                                 WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        for (Line line : lines) {
            buildGraphFromSections(graph, line.getAllSections());
        }
        return graph;
    }

    private void buildGraphFromSections(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();

            graph.addVertex(upStation);
            graph.addVertex(downStation);
            graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
        }
    }

    private boolean isPathNotFound(GraphPath<Station, DefaultWeightedEdge> path) {
        return path == null;
    }

    private PathResult generatePathResult(GraphPath<Station, DefaultWeightedEdge> path) {
        return new PathResult(path.getVertexList(), (int) path.getWeight());
    }

    private GraphPath<Station, DefaultWeightedEdge> findShortestPath(Station departureStation,
                                                                     Station arrivalStation,
                                                                     WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        return new DijkstraShortestPath<>(graph).getPath(departureStation, arrivalStation);
    }
}
