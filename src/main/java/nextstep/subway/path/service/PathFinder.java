package nextstep.subway.path.service;

import nextstep.subway.line.domain.Sections;
import nextstep.subway.path.controller.dto.PathResponse;
import nextstep.subway.path.exception.PathException;
import nextstep.subway.station.controller.dto.StationResponse;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class PathFinder {

    public PathResponse findPath(
        List<Sections> sectionsList,
        Station source,
        Station target
    ) {
        try {
            GraphPath<Station, DefaultWeightedEdge> path = getShortestPath(
                createGraph(sectionsList),
                source,
                target
            );

            List<Station> stations = path.getVertexList();

            return new PathResponse(
                stations.stream().map(StationResponse::of).collect(Collectors.toList()),
                (int) path.getWeight()
            );
        } catch (Exception e) {
            throw new PathException("경로조회에 실패했습니다.");
        }
    }

    private GraphPath<Station, DefaultWeightedEdge> getShortestPath(
        WeightedMultigraph<Station, DefaultWeightedEdge> graph,
        Station source,
        Station target
    ) {
        return new DijkstraShortestPath<>(graph).getPath(source, target);
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createGraph(List<Sections> sectionsList) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        sectionsList.stream()
            .flatMap(sections -> sections.getSections().stream())
            .forEach(section -> {
                graph.addVertex(section.getUpStation());
                graph.addVertex(section.getDownStation());

                DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
                graph.setEdgeWeight(edge, section.getDistance());
            });

        return graph;
    }
}
