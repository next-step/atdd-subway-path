package nextstep.subway.domain;

import lombok.RequiredArgsConstructor;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

@RequiredArgsConstructor
public class PathFinderFactory {
    public static PathFinder createPathFinder(List<Section> allSections, List<Station> allStations) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        allStations.forEach(graph::addVertex);
        allSections.forEach(section -> graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getSectionDistance().getDistance()
        ));
        return new PathFinder(graph);
    }
}
