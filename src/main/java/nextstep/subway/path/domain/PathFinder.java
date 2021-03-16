package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.DoseNotExistedStationException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathFinder {

    private PathFinder () {}

    public static GraphPath<Station, DefaultWeightedEdge> getShortedPath(
        List<Section> sections,
        Station source,
        Station target
    ) {
        List<Station> allStations = sections.stream()
            .flatMap(section -> Stream.of(
                section.getUpStation(),
                section.getDownStation()
            ))
            .distinct()
            .collect(Collectors.toList());

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        if (!allStations.containsAll(Arrays.asList(source, target))) {
            throw new DoseNotExistedStationException();
        }

        for (Station station : allStations) {
            graph.addVertex(station);
        }

        for (Section section : sections) {
            graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance()
            );
        }

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return dijkstraShortestPath.getPath(source, target);
    }
}
