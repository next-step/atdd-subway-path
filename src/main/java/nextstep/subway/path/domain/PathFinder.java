package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Section;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PathFinder {

    private List<Section> sections;
    private Set<Station> stations;
    private DijkstraShortestPath dijkstraShortestPath;

    public PathFinder(List<Section> sections) {
        validateSections(sections);

        this.sections = sections;
        this.dijkstraShortestPath = setPath();
    }

    public PathResponse getPath(Station source, Station target) {
        validate(source, target);

        GraphPath graphPath = dijkstraShortestPath.getPath(source, target);
        if (graphPath == null) {
            throw new RuntimeException("경로가 존재하지 않습니다.");
        }

        List<Station> shortestPath = graphPath.getVertexList();
        List<StationResponse> paths = shortestPath.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        int distance = (int) graphPath.getWeight();

        return new PathResponse(paths, distance);
    }

    private void validate(Station source, Station target) {
        if (!stations.contains(source) || !stations.contains(target)) {
            throw new RuntimeException("구간정보에 등록된 출발역(도착역)이 없습니다.");
        }

        if (source.equals(target)) {
            throw new RuntimeException("출발역과 도착역이 같습니다.");
        }
    }

    private void validateSections(List<Section> sections) {
        if (sections == null || sections.isEmpty()) {
            throw new RuntimeException(" 구간 정보가 존재하지 않습니다.");
        }
    }

    private DijkstraShortestPath setPath() {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        this.stations = getStations();

        addVertex(graph);
        addEdge(graph);
        return new DijkstraShortestPath(graph);
    }

    private void addVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        this.stations.forEach(graph::addVertex);
    }

    private void addEdge(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        this.sections.forEach(section -> {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance().getDistance());
        });
    }

    private Set<Station> getStations() {
        Set<Station> stations = new HashSet<>();
        this.sections.forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });

        return stations;
    }
}
