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

public class DijkstraPathFinder extends PathFinder {
    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    public DijkstraPathFinder(List<Station> stations, List<Section> sections) {
        super(stations, sections);
    }

    @Override
    public void init() {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        getStations().forEach(graph::addVertex);
        getSections().forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
        dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    }

    @Override
    public PathResponse findPath(Station source, Station target) {
        if (graph == null || dijkstraShortestPath == null) {
            throw new IllegalArgumentException("init 메서드로 먼저 초기화를 해주세요.");
        }
        if (source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역이 같습니다.");
        }
        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            throw new IllegalArgumentException("존재하지 않는 역을 입력하였습니다.");
        }
        GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(source, target);
        if (path == null) {
            throw new IllegalArgumentException("출발역과 도착역이 이어져있지 않습니다");
        }
        List<PathStationResponse> stations = path.getVertexList()
                .stream()
                .map(PathStationResponse::new)
                .collect(toList());
        long distance = (long) path.getWeight();
        return new PathResponse(stations, distance);
    }

}
