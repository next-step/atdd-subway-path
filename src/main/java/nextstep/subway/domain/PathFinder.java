package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public interface PathFinder {
    WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
    GraphPath<Long, String> getPath(Long source, Long target);

    default void registLine(Line line) {
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
