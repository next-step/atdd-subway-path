package nextstep.subway.domain;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public abstract class AbstractPathFinder {
    protected WeightedMultigraph<Long, DefaultWeightedEdge> graph;

    public AbstractPathFinder(List<Line> lines) {
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        lines.forEach(this::registLine);
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
