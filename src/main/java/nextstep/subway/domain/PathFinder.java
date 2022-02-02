package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.PathStationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toSet;

@Component
public class PathFinder {
    public PathResponse shortestPath(List<Line> lines, Station source, Station target) {
        checkSourceAndTargetNotSame(source, target);
        return createPath(getSourceToTargetPath(lines, source, target));
    }


    private void checkSourceAndTargetNotSame(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("출발지와 도착지가 같아 경로를 조회할 수 없습니다.");
        }
    }

    private GraphPath getSourceToTargetPath(List<Line> lines, Station source, Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        Set<Station> stations = lines.stream().map(Line::getStations).flatMap(Collection::stream).collect(toSet());
        Set<Section> sections = lines.stream().map(Line::getSections).flatMap(Collection::stream).collect(toSet());
        stations.forEach(station -> {
            graph.addVertex(station);
        });
        sections.forEach(section -> {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        });
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath path = dijkstraShortestPath.getPath(source, target);
        return path;
    }

    private PathResponse createPath(GraphPath path) {
        if (path == null) {
            throw new IllegalArgumentException("출발지와 도착지가 연결되지 않아서 경로를 조회할 수 없습니다.");
        }
        List<Station> shortestPath = path.getVertexList();
        int pathWeight = (int) path.getWeight();

        return new PathResponse(createPathStation(shortestPath), pathWeight);
    }

    private List<PathStationResponse> createPathStation(List<Station> stations) {
        return stations.stream().map(PathStationResponse::new)
                .collect(toList());
    }
}
