package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;

public class PathFinder {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;
    private final Station source;
    private final Station target;

    public PathFinder(final Station source, final Station target, final List<Line> lines) {

        if (source.equals(target)) {
            throw new DataIntegrityViolationException("출발역과 도착역이 같을 수 없습니다.");
        }

        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        lines.stream().map(Line::getSections).forEach(
            sections -> sections.getSections().forEach(section -> {
                {
                    addVertex(section.getUpStation());
                    addVertex(section.getDownStation());
                    setEdgeWeight(section);
                }
            })
        );
        dijkstraShortestPath = new DijkstraShortestPath<>(graph);

        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            throw new DataIntegrityViolationException("역이 존재하지 않습니다.");
        }

        if (dijkstraShortestPath.getPath(source, target) == null) {
            throw new DataIntegrityViolationException("연결되지 않은 역입니다.");
        }

        this.source = source;
        this.target = target;
    }

    public List<Station> getPath() {
        return new ArrayList<>(dijkstraShortestPath.getPath(source, target).getVertexList());
    }

    public int getDistance() {
        return (int) dijkstraShortestPath.getPathWeight(source, target);
    }

    private void setEdgeWeight(final Section section) {
        graph.setEdgeWeight(
            graph.addEdge(section.getUpStation(), section.getDownStation()),
            section.getDistance()
        );
    }

    private boolean addVertex(final Station station) {
        return graph.addVertex(station);
    }

}
