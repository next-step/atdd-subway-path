package nextstep.subway.vo;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.PathStationResponse;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class PathFinder {
    private DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    public PathFinder(List<Station> stations, List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        stations.forEach(graph::addVertex);
        sections.forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
        dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    }

    public PathResponse findPath(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역이 같습니다.");
        }
        GraphPath<Station, DefaultWeightedEdge> path;
        try {
            path = dijkstraShortestPath.getPath(source, target);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("존재하지 않는 역입니다.");
        }
        if (path == null) {
            throw new IllegalArgumentException();
        }
        List<PathStationResponse> stations = path.getVertexList()
                .stream()
                .map(PathStationResponse::new)
                .collect(toList());
        long distance = (long) path.getWeight();
        return new PathResponse(stations, distance);
    }
}
