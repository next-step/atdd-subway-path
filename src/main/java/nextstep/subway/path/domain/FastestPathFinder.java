package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FastestPathFinder extends PathFinder {
    @Override
    public List<LineStation> findPath(List<Line> lines, Long sourceStationId, Long targetStationId) {
        Map<Long, LineStation> lineStations = this.getLineStations(lines);
        List<Long> shortestPathStationIds = this.getShortestPath(lineStations, sourceStationId, targetStationId);
        return this.toLineStations(lineStations, shortestPathStationIds);
    }

    private Map<Long, LineStation> getLineStations(List<Line> lines) {
        return lines.stream()
                .flatMap(it -> it.getLineStations().getLineStations().stream())
                .collect(Collectors.toMap(it -> it.getStationId(), it -> it, (it1, it2) -> it1));
    }

    private List<Long> getShortestPath(Map<Long, LineStation> lineStations, Long sourceStationId, Long targetStationId) {
        if (!lineStations.containsKey(sourceStationId) || !lineStations.containsKey(targetStationId)) {
            throw new CannotFindPathException();
        }

        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        lineStations.forEach((id, it) -> graph.addVertex(id));
        lineStations.forEach((id, it) -> {
            if (Objects.nonNull(it.getPreStationId())) {
                graph.setEdgeWeight(graph.addEdge(it.getStationId(), it.getPreStationId()), it.getDuration()); // FIXME only diff with ShortestPathFinder
            }
        });

        List<GraphPath<Long, DefaultWeightedEdge>> paths = new KShortestPaths<>(graph, 1).getPaths(sourceStationId, targetStationId);

        if (paths.isEmpty()) {
            throw new CannotFindPathException();
        }

        return paths.get(0).getVertexList();
    }

    private List<LineStation> toLineStations(Map<Long, LineStation> lineStations, List<Long> shortestPathStationIds) {
        return shortestPathStationIds.stream()
                .map(lineStations::get)
                .collect(Collectors.toList());
    }
}
