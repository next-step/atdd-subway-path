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


    public PathFinder(List<Section> sections) {
        init(sections);
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

    public GraphPath<Station, DefaultWeightedEdge> getShortestPath(Station source, Station target) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> shortPaths = dijkstraShortestPath.getPath(source, target);
        if (Objects.isNull(shortPaths)) {
            throw new BadRequestPathException("출발역과 도착역 사이의 경로가 존재하지 않습니다.");
        }
        return shortPaths;
    }
}
