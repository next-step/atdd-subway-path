package nextstep.subway.support;

import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import nextstep.subway.applicaion.line.LineGraphService;
import nextstep.subway.domain.line.LineSections;
import nextstep.subway.domain.station.Station;

@Component
public class LineJGraphService implements LineGraphService {

    @Override
    public List<Station> orderedStations(final LineSections sections) {
        final var firstStationNode = sections.getFirstStation().getId();
        final var lastStationNode = sections.getLastStation().getId();

        final var dijkstraShortestPath = new DijkstraShortestPath<>(initGraph(sections));
        final var nodes = dijkstraShortestPath.getPath(firstStationNode, lastStationNode).getVertexList();

        return mapToStations(sections, nodes);
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> initGraph(final LineSections sections) {
        final var graph = new WeightedMultigraph<Long, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        for (final var station : sections.getStations()) {
            graph.addVertex(station.getId());
        }

        for (final var section : sections.getValue()) {
            graph.setEdgeWeight(
                    graph.addEdge(section.getUpStation().getId(), section.getDownStation().getId()),
                    section.getDistance()
            );
        }

        return graph;
    }

    private List<Station> mapToStations(final LineSections sections, final List<Long> nodes) {
        final var stations = sections.getStations().stream()
                .collect(Collectors.toUnmodifiableMap(Station::getId, it -> it));

        return nodes.stream()
                .map(stations::get)
                .collect(Collectors.toUnmodifiableList());
    }
}
