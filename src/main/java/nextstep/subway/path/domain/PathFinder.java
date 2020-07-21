package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PathFinder {

    private Map<Long, LineStation> lineStationByStationId;
    private WeightedMultigraph<Long, DefaultWeightedEdge> graph;

    public PathFinder(List<Line> lines) {
        this.lineStationByStationId = lines.stream()
                .map(Line::getLineStations)
                .flatMap(lineStations -> lineStations.getLineStations().stream())
                .collect(Collectors.toMap(LineStation::getStationId, Function.identity(), (id1, id2) -> id2));

        initGraph(lines);
    }

    private void initGraph(List<Line> lines) {
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        addVertexes(lines);
        addEdges(lines);
    }

    private void addVertexes(List<Line> lines) {
        lines.stream()
                .map(Line::getLineStations)
                .flatMap(lineStations -> lineStations.getLineStations().stream())
                .map(LineStation::getStationId)
                .forEach(stationId -> graph.addVertex(stationId));
    }

    private void addEdges(List<Line> lines) {
        lines.stream()
                .map(Line::getLineStations)
                .flatMap(lineStations -> lineStations.getLineStations().stream())
                .filter(lineStation -> lineStation.getPreStationId() != null)
                .forEach(lineStation -> {
                    DefaultWeightedEdge weightedEdge = graph.addEdge(lineStation.getPreStationId(), lineStation.getStationId());
                    graph.setEdgeWeight(weightedEdge,  lineStation.getDistance());
                });
    }

    public List<LineStation> searchShortestPath(Long sourceStationId, Long targetStationId) {
        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        List<Long> shortestStationIds = dijkstraShortestPath.getPath(sourceStationId, targetStationId).getVertexList();

        return shortestStationIds.stream()
                .map(stationId -> lineStationByStationId.get(stationId))
                .collect(Collectors.toList());
    }
}
