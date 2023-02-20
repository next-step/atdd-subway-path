package nextstep.subway.domain;

import nextstep.subway.exception.CustomException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    private WeightedMultigraph<String, DefaultWeightedEdge> graph;
    private DijkstraShortestPath dijkstraShortestPath;

    public PathFinder(List<Line> lines) {
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        lines.forEach(this::registLine);
        dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    }

    public GraphPath<String, String> getShortestPath(Long source, Long target) {
        try {
            return dijkstraShortestPath.getPath(String.valueOf(source), String.valueOf(target));
        } catch (IllegalArgumentException e) {
            //예외 번역
            throw new CustomException(CustomException.PATH_MUST_CONTAIN_STATION);
        }
    }

    private void registLine(Line line) {
        //역 등록
        line.getStations().stream()
                        .map(station -> String.valueOf(station.getId()))
                        .forEach(graph::addVertex);

        //구간 등록
        line.getSections()
                    .stream()
                    .forEach(section ->
                            graph.setEdgeWeight(
                                    graph.addEdge(
                                        String.valueOf(section.getUpStation().getId()),
                                        String.valueOf(section.getDownStation().getId())),
                                    section.getDistance()));
    }
}
