package nextstep.subway.path;

import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.Station;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class PathFinder {

    public PathResponse getPath(Sections sections, Station sourceStation, Station targetStation) {

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        for (Section section : sections.getSections()) {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
            graph.setEdgeWeight(edge, section.getDistance());
        }

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<Station> stations = dijkstraShortestPath.getPath(sourceStation, targetStation).getVertexList();
        double distance = dijkstraShortestPath.getPath(sourceStation, targetStation).getWeight();

        return new PathResponse(
                stations.stream().map(StationResponse::ofEntity).collect(Collectors.toList()),
                (int) distance
        );
    }
}
