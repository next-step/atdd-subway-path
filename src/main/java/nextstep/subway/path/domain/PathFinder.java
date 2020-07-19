package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.domain.LineStations;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PathFinder {
    WeightedMultigraph graph;

    public PathFinder() {
        this.graph = new WeightedMultigraph(DefaultWeightedEdge.class);;
    }

    public List<LineStation> findShortestPath(List<Line> lines, Long source, Long target) {
        Map<Long, LineStation> lineStationsWithId = getLineStationsWithId(lines);

        addVertexs(lineStationsWithId, graph);
        addEdges(lineStationsWithId, graph);

        List<Long> shortestPathIds = getShortestPath(source, target);

        return toLineStations(lineStationsWithId, shortestPathIds);
    }

    private Map<Long, LineStation> getLineStationsWithId(List<Line> lines) {
        List<LineStations> lineStations = lines.stream()
                .map(Line::getLineStations)
                .collect(Collectors.toList());

        // line
        return lineStations
                .stream()
                .flatMap(it -> it.getLineStations().stream())
                .collect(Collectors.toMap(LineStation::getStationId, Function.identity()));
    }

    private void addVertexs(Map<Long, LineStation> lineStationsWithId, WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        lineStationsWithId.forEach((id, it) -> {
            graph.addVertex(id);
        });
    }

    private void addEdges(Map<Long, LineStation> lineStationsWithId, WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        lineStationsWithId.forEach((id, it) -> {
            if (it.getPreStationId() != null) {
                graph.setEdgeWeight(graph.addEdge(it.getStationId(), it.getPreStationId()), it.getDistance());
            }
        });
    }

    private List<Long> getShortestPath(Long source, Long target) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        return (List<Long>) dijkstraShortestPath.getPath(source, target).getVertexList();
    }

    private List<LineStation> toLineStations(Map<Long, LineStation> lineStationsWithId, List<Long> shortestPathIds) {
        return shortestPathIds.stream()
                .map(lineStationsWithId::get)
                .collect(Collectors.toList());
    }
}
