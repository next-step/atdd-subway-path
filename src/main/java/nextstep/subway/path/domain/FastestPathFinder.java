package nextstep.subway.path.domain;

import nextstep.subway.line.domain.LineStation;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FastestPathFinder extends PathFinder {
    @Override
    protected List<Long> getPath(Map<Long, LineStation> lineStations, Long sourceStationId, Long targetStationId) {
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
}
