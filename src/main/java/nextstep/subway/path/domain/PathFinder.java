package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    private WeightedMultigraph<Long, DefaultWeightedEdge> graph;

    public PathFinder(List<Line> lines) {
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

    public List<Long> searchShortestPath(Long sourceStationId, Long targetStationId) {
        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return dijkstraShortestPath.getPath(sourceStationId, targetStationId).getVertexList();
    }
}
