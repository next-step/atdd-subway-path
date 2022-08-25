package nextstep.subway.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

@NoArgsConstructor
@Getter
public class PathFinder {

    DijkstraShortestPath<Station, DefaultWeightedEdge> subwayGraph;

    private PathFinder(List<Line> lines) {
        this.subwayGraph = new DijkstraShortestPath<>(createSubwayGraph(lines));
    }

    public static PathFinder of(List<Line> lines) {
        return new PathFinder(lines);
    }

    public Path paths(Station source, Station target) {
        validSearchPath(source, target);
        GraphPath<Station, DefaultWeightedEdge> paths = subwayGraph.getPath(source, target);
        if (paths == null) {
            throw new IllegalArgumentException();
        }
        return new Path(paths.getVertexList(), (int) paths.getWeight());
    }

    private void validSearchPath(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역이 같을 수 없습니다.");
        }
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createSubwayGraph(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> subwayGraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        lines.forEach(line -> createSectionGraph(subwayGraph, line.sections()));
        return subwayGraph;
    }

    private void createSectionGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        for (Section section : sections) {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }
    }

}
