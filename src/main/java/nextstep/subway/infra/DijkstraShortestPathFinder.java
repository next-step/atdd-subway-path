package nextstep.subway.infra;

import java.util.List;
import java.util.Set;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.ShortestPathFinder;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

@Component
public class DijkstraShortestPathFinder implements ShortestPathFinder {

    @Override
    public Path findShortestPath(List<Line> lines, Long source, Long target) {
        final DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = shortestPath(lines);

        final GraphPath<Long, DefaultWeightedEdge> shortestPath;
        try {
            shortestPath = dijkstraShortestPath.getPath(source, target);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("존재하지 않는 출발역이나 도착역입니다.");
        }

        if(shortestPath == null) {
            throw new IllegalArgumentException("출발역과 도착역이 연결되어 있지 않습니다");
        }

        final List<Long> stationIds = shortestPath.getVertexList();
        final Double distance = shortestPath.getWeight();
        return new Path(stationIds, distance.longValue());
    }

    private DijkstraShortestPath<Long, DefaultWeightedEdge> shortestPath(List<Line> lines) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        addLines(graph, lines);

        return new DijkstraShortestPath<>(graph);
    }

    private void addLines(WeightedMultigraph<Long, DefaultWeightedEdge> graph, List<Line> lines) {
        for (Line line : lines) {
            addSections(graph, line.getSections().getElements());
        }
    }

    private void addSections(WeightedMultigraph<Long, DefaultWeightedEdge> graph, Set<Section> sections) {
        for (Section section : sections) {
            final Long upStationId = section.getUpStationId();
            final Long downStationId = section.getDownStationId();

            graph.addVertex(upStationId);
            graph.addVertex(downStationId);
            graph.setEdgeWeight(
                graph.addEdge(upStationId, downStationId),
                section.getDistance()
            );
        }
    }
}
