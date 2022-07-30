package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.ArrayList;
import java.util.List;

public class PathFinder {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;
    private Station source;
    private Station target;

    public PathFinder(Station source, Station target, List<Line> lines) {

        if (source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역이 같을 수 없습니다.");
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

        if (dijkstraShortestPath.getPath(source, target) == null) {
            throw new IllegalArgumentException("연결되지 않은 역입니다.");
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

    private void setEdgeWeight(Section section) {
        graph.setEdgeWeight(
            graph.addEdge(section.getUpStation(), section.getDownStation()),
            section.getDistance()
        );
    }

    private boolean addVertex(Station station) {
        return graph.addVertex(station);
    }

}
