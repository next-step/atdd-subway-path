package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.PathStationResponse;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toSet;

@Component
public class PathFinder {
    public PathResponse shortestPath(List<Line> lines, Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("출발지와 도착지가 같아 경로를 조회할 수 없습니다.");
        }

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        Set<Station> stations = lines.stream().map(Line::getStations).flatMap(Collection::stream).collect(toSet());
        Set<Section> sections = lines.stream().map(Line::getSections).flatMap(Collection::stream).collect(toSet());


        stations.forEach(station -> {
            graph.addVertex(station);
        });
        sections.forEach(section -> {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        });


        if (!graph.containsEdge(source, target)) {
            throw new IllegalArgumentException("출발지와 도착지가 연결되지 않아서 경로를 조회할 수 없습니다.");
        }

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<Station> shortestPath = dijkstraShortestPath.getPath(source, target).getVertexList();
        int pathWeight = (int) dijkstraShortestPath.getPathWeight(source, target);
        return createPath(shortestPath, pathWeight);
    }

    private PathResponse createPath(List<Station> stations,int distance) {
        return new PathResponse(createPathStation(stations),distance);
    }

    private List<PathStationResponse> createPathStation(List<Station> stations) {
        return stations.stream().map(PathStationResponse::new)
                .collect(toList());
    }
}
