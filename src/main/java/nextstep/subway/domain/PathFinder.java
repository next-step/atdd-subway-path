package nextstep.subway.domain;

import nextstep.subway.exception.CustomException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    private WeightedMultigraph<Long, DefaultWeightedEdge> graph;
    private DijkstraShortestPath dijkstraShortestPath;

    public PathFinder(List<Line> lines) {
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        lines.forEach(this::registLine);
        dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    }

    public GraphPath<Long, String> getShortestPath(Long source, Long target) {
        try {
            return dijkstraShortestPath.getPath(source, target);
        } catch (IllegalArgumentException e) {
            //예외 번역
            throw new CustomException(CustomException.PATH_MUST_CONTAIN_STATION);
        }
    }

    private void registLine(Line line) {
        //역 등록
        line.getStations().stream()
                        .map(station -> station.getId())
                        .forEach(graph::addVertex);

        //구간 등록
        line.getSections()
                    .stream()
                    .forEach(section ->
                            graph.setEdgeWeight(
                                    graph.addEdge(
                                        section.getUpStation().getId(),
                                        section.getDownStation().getId()),
                                    section.getDistance()));
    }
}
