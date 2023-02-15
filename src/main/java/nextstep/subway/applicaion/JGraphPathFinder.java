package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class JGraphPathFinder implements PathFinder {
    @Override
    public Path find(List<Line> lines, Station source, Station target) {
        WeightedMultigraph<Long, DefaultWeightedEdge> subwayGraph = makeSubwayGraph(lines);
        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(subwayGraph);
        int distance = (int) dijkstraShortestPath.getPathWeight(source.getId(), target.getId());
        List<Long> paths = dijkstraShortestPath.getPath(source.getId(), target.getId()).getVertexList();
        return new Path(paths, distance);
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> makeSubwayGraph(List<Line> lines) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .forEach(section -> {
                    Long upStationId = section.getUpStationId();
                    Long downStationId = section.getDownStationId();
                    int distance = section.getDistance();
                    if (!graph.containsVertex(upStationId)) {
                        graph.addVertex(upStationId);
                    }
                    if (!graph.containsVertex(downStationId)) {
                        graph.addVertex(downStationId);
                    }
                    graph.setEdgeWeight(graph.addEdge(upStationId, downStationId), distance);
                });
        return graph;
    }
}
