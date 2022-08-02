package nextstep.subway.applicaion;

import java.util.List;
import java.util.Objects;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathFinder {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathFinder(List<Section> sections) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        sections.forEach(section -> {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
            }
        );
    }

    public Path solve(Station source, Station target) {
        if (Objects.equals(source, target)) {
            throw new IllegalArgumentException();
        }
        var path = new DijkstraShortestPath(graph).getPath(source, target);
        if (path == null) {
            throw new IllegalStateException("출발역과 도착역 사이의 경로가 없습니다.");
        }
        return new Path(path.getVertexList(), (int) path.getWeight());
    }
}
