package subway.path.component;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;
import subway.exception.SubwayBadRequestException;
import subway.line.model.Section;
import subway.path.dto.PathRetrieveResponse;
import subway.station.dto.StationResponse;
import subway.station.model.Station;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PathFinder {

    // TODO: 단위 테스트 - 모킹
    public PathRetrieveResponse findShortestPath(List<Section> sections,
                                                 Station sourceStation,
                                                 Station targetStation) {
        List<Station> stations = getStations(sections);
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = getDistanceGraph(sections, stations);
        List<Station> stationsInShortestPath = getShortestPath(graph, sourceStation, targetStation);
        Double minimumWeight = getWeightOfShortestPath(graph, sourceStation, targetStation);

        return PathRetrieveResponse.builder()
                .stations(StationResponse.from(stationsInShortestPath))
                .distance(minimumWeight.longValue())
                .build();
    }

    private Double getWeightOfShortestPath(WeightedMultigraph<Station, DefaultWeightedEdge> graph,
                                           Station sourceStation,
                                           Station targetStation) {
        try {
            DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
            return dijkstraShortestPath.getPathWeight(sourceStation, targetStation);
        } catch (NullPointerException e) {
            throw new SubwayBadRequestException(9999L, "역이 연결되어 있지 않습니다."); // TODO: constant
        }
    }

    private List<Station> getShortestPath(WeightedMultigraph<Station, DefaultWeightedEdge> graph,
                                          Station sourceStation,
                                          Station targetStation) {
        try {
            DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
            return dijkstraShortestPath.getPath(sourceStation, targetStation).getVertexList();
        } catch (NullPointerException e) {
            throw new SubwayBadRequestException(9999L, "역이 연결되어 있지 않습니다.");
        }
    }

    private List<Station> getStations(List<Section> sections) {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toList());
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> getDistanceGraph(List<Section> sections, List<Station> stations) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        stations.forEach(graph::addVertex);
        sections.forEach(section ->
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
        return graph;
    }
}
