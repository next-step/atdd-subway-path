package nextstep.subway.support;

import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import nextstep.subway.applicaion.LineGraphService;
import nextstep.subway.applicaion.StationNode;
import nextstep.subway.domain.line.LineSections;
import nextstep.subway.domain.station.Station;

@Component
public class LineJGraphService implements LineGraphService {

    @Override
    public List<Station> orderedStations(final LineSections sections) {
        final var firstStationNode = new StationNode(sections.getFirstStation());
        final var lastStationNode = new StationNode(sections.getLastStation());

        final var dijkstraShortestPath = new DijkstraShortestPath<>(initGraph(sections));
        final var nodes = dijkstraShortestPath.getPath(firstStationNode, lastStationNode).getVertexList();

        return nodes.stream().map(StationNode::getStation).collect(Collectors.toUnmodifiableList());
    }

    private WeightedMultigraph<StationNode, DefaultWeightedEdge> initGraph(final LineSections sections) {
        final var graph = new WeightedMultigraph<StationNode, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        for (final var station : sections.getStations()) {
            graph.addVertex(new StationNode(station));
        }

        for (final var section : sections.getValue()) {
            graph.setEdgeWeight(
                    graph.addEdge(new StationNode(section.getUpStation()), new StationNode(section.getDownStation())),
                    section.getDistance()
            );
        }

        return graph;
    }
}
