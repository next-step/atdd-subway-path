package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PathFinder {

    /*
    정점(vertext)과 간선(edge), 그리고 가중치 개념을 이용
    정점: 지하철역(Station)
    간선: 지하철역 연결정보(Section)
    가중치: 거리
    */
    public PathResponse findPath(List<Line> lines, Map<Long, Station> stationMap, Long source, Long target) {
        checkValidate(stationMap, source, target);

        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        stationMap.forEach((stationId, station) ->
                graph.addVertex(stationId)
        );
        lines.stream().flatMap(it -> it.getSectionList().stream())
                .forEach(it ->
                        graph.setEdgeWeight(graph.addEdge(it.getUpStation().getId(), it.getDownStation().getId()), it.getDistance())
                );

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath result = dijkstraShortestPath.getPath(source, target);
        if (result == null) {
            throw new RuntimeException("출발역과 도착역이 연결이 되어 있지 않습니다.");
        }

        List<StationResponse> stationResponseList = ((List<Long>) result.getVertexList()).stream()
                .map(it -> StationResponse.of(stationMap.get(it)))
                .collect(Collectors.toList());

        return new PathResponse(stationResponseList, result.getWeight());
    }

    private void checkValidate(Map<Long, Station> stationMap, Long source, Long target) {
        if (source == target) {
            throw new RuntimeException("출발역과 도착역이 같습니다.");
        }
        if (!stationMap.containsKey(source)) {
            throw new RuntimeException("출발역이 존재하지 않습니다.");
        }
        if (!stationMap.containsKey(target)) {
            throw new RuntimeException("도착역이 존재하지 않습니다.");
        }
    }
}
