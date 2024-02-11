package nextstep.subway.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class JGraphTPathFinderImpl implements PathFinder {
    @Override
    public PathResponse findPath(PathRequest pathRequest, List<Line> lines) {
        validateRequest(pathRequest, lines);

        final Set<Section> sections = getAllSectionsInLines(lines);
        final WeightedMultigraph<String, DefaultWeightedEdge> sectionGraph = getWightedGraphWithSection(sections);

        final DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(sectionGraph);

        final GraphPath<String, DefaultWeightedEdge> graphPath = dijkstraShortestPath.getPath(pathRequest.getSource().toString(), pathRequest.getTarget().toString());

        return getPathResponse(sections, graphPath.getVertexList(), (int) graphPath.getWeight());
    }

    @Override
    public void validateRequest(PathRequest request, List<Line> lines) {
        if (request.getSource().equals(request.getTarget())) {
            throw new IllegalArgumentException("출발역과 도착역이 같습니다.");
        }

        final List<Station> stations = getAllStationsInLines(lines);

        if (stations.stream().noneMatch(it -> it.getId().equals(request.getSource()))) {
            throw new IllegalArgumentException("출발역이 존재하지 않습니다.");
        }

        if(stations.stream().noneMatch(it -> it.getId().equals(request.getTarget()))) {
            throw new IllegalArgumentException("도착역이 존재하지 않습니다.");
        }
    }

    private PathResponse getPathResponse(Set<Section> sections, List<String> stationPath, int distance) {
        final Map<String, Station> stationMap = getIdToStationMap(sections);

        return new PathResponse(
            stationPath.stream().map(stationMap::get).collect(Collectors.toList())
            , distance
        );
    }

    private Map<String, Station> getIdToStationMap(Set<Section> sections) {
        final Map<String, Station> stationMap = new HashMap<>();

        sections.forEach(section -> {
            stationMap.put(section.getUpStation().getId().toString(), section.getUpStation());
            stationMap.put(section.getDownStation().getId().toString(), section.getDownStation());
        });

        return stationMap;
    }

    private static WeightedMultigraph<String, DefaultWeightedEdge> getWightedGraphWithSection(Set<Section> sections) {
        final WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        sections.forEach(section -> {
            final String upStationId = section.getUpStation().getId().toString();
            final String downStationId = section.getDownStation().getId().toString();

            graph.addVertex(upStationId);
            graph.addVertex(downStationId);

            DefaultWeightedEdge edge = graph.addEdge(upStationId, downStationId);
            graph.setEdgeWeight(edge, section.getDistance());
        });

        return graph;
    }

    private Set<Section> getAllSectionsInLines(List<Line> lines) {
        return lines.stream().flatMap(line -> line.getSections().stream())
            .collect(Collectors.toSet());
    }

    private static List<Station> getAllStationsInLines(List<Line> lines) {
        return lines.stream()
            .flatMap(it -> it.getStations().stream())
            .collect(Collectors.toList());
    }
}
