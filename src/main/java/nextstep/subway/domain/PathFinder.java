package nextstep.subway.domain;

import nextstep.subway.exception.IllegalPathArgumentException;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;


public class PathFinder {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    private DijkstraShortestPath dijkstraShortestPath;

    public PathFinder(List<Line> lines) {
        lines.stream()
                .map(line -> line.getSections())
                .flatMap(sections -> sections.stream())
                .forEach(this::setEdgeWeight);

        dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    public List<Station> getShortestPath(Station source, Station target) {
        validateStation(source, target);

        return dijkstraShortestPath.getPath(source, target).getVertexList();
    }

    public int getShortestDistance(Station source, Station target) {
        validateStation(source, target);
        return (int) dijkstraShortestPath.getPath(source, target).getWeight();
    }

    private void setEdgeWeight(Section section) {
        Station downStation = section.getDownStation();
        Station upStation = section.getUpStation();

        graph.addVertex(upStation);
        graph.addVertex(downStation);
        graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
    }

    private void validateStation(Station source, Station target) {
        if (Objects.equals(source, target)) {
            throw new IllegalPathArgumentException("출발역과 도착역이 같을 수 없습니다.");
        }
    }
}
