package nextstep.subway.domain;

import nextstep.subway.exception.BadRequestPathException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Objects;

public class PathFinder {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    public PathFinder(List<Section> sections) {
        this.dijkstraShortestPath = init(sections);
    }

    private DijkstraShortestPath<Station, DefaultWeightedEdge> init(List<Section> sections) {
        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            graph.addVertex(upStation);
            graph.addVertex(downStation);
            graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
        }

        return new DijkstraShortestPath<>(graph);
    }

    public Path getShortestPath(Station source, Station target) {
        GraphPath<Station, DefaultWeightedEdge> shortPaths = dijkstraShortestPath.getPath(source, target);
        if (Objects.isNull(shortPaths)) {
            throw new BadRequestPathException("출발역과 도착역 사이의 경로가 존재하지 않습니다.");
        }
        return new Path(shortPaths.getVertexList(), (int) shortPaths.getWeight());
    }
}
