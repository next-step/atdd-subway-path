package nextstep.subway.path.domain;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.path.dto.PathFinderResult;
import nextstep.subway.path.ui.FindType;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    public PathFinder() {
    }

    public PathFinderResult findPath(List<LineResponse> lineResponses, long srcStationId, long dstStationId, FindType type) {
        final WeightedMultigraph<Long, DefaultWeightedEdge> subwayGraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        // 정점(역)을 추가한다.
        lineResponses.stream().flatMap(it -> it.getStations().stream())
                .map(LineStationResponse::getStation)
                .distinct()
                .map(StationResponse::getId)
                .forEach(subwayGraph::addVertex);

        // 간선 정보 추가(가중치: 거리 혹은 시간)
        lineResponses.stream().flatMap(it -> it.getStations().stream())
                .filter(lineStationResponse -> lineStationResponse.getPreStationId() != null)
                .forEach(it ->
                        subwayGraph.setEdgeWeight(
                                subwayGraph.addEdge(it.getStation().getId(), it.getPreStationId()),
                                getWeightByType(it, type)
                        )
                );

        final DijkstraShortestPath<Long, DefaultWeightedEdge> paths = new DijkstraShortestPath<>(subwayGraph);
        final GraphPath<Long, DefaultWeightedEdge> result = paths.getPath(srcStationId, dstStationId);
        return new PathFinderResult(result.getVertexList(), result.getWeight());
    }

    // fixme: Enum 안에 Lambda로 넣으면 예쁘게 빠질텐데 일단 귀찮아서
    private double getWeightByType(LineStationResponse response, FindType type) {
        if (FindType.DISTANCE.equals(type)) {
            return response.getDistance();
        }
        return response.getDuration();
    }
}
