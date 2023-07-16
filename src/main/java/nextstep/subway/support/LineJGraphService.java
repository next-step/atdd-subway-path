package nextstep.subway.support;

import java.util.List;

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
        final var firstStationNode = sections.getFirstStation();
        final var lastStationNode = sections.getLastStation();

        final var dijkstraShortestPath = new DijkstraShortestPath<>(initGraph(sections));
        return dijkstraShortestPath.getPath(firstStationNode, lastStationNode).getVertexList();
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> initGraph(final LineSections sections) {
        final var graph = new WeightedMultigraph<Station, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        for (final var station : sections.getStations()) {
            graph.addVertex(station);
        }

        for (final var section : sections.getValue()) {
            graph.setEdgeWeight(
                    graph.addEdge(section.getUpStation(), section.getDownStation()),
                    section.getDistance()
            );
        }

        return graph;
    }
}
