package nextstep.subway.domain;

import nextstep.subway.exception.IllegalPathArgumentException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Objects;


public class PathFinder {
    public static final String NOT_EQUALS_STATION = "출발역과 도착역이 같을 수 없습니다.";
    public static final String NOT_EXISTS_STATION = "존재하지 않은 출발역이나 도착역을 조회할수 없습니다.";
    public static final String NOT_EXISTS_STATION_EDGE = "출발역과 도착역이 연결이 되어 있지 않습니다.";

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph =
            new WeightedMultigraph(DefaultWeightedEdge.class);
    private final DijkstraShortestPath dijkstraShortestPath;

    public PathFinder(List<Line> lines) {
        lines.stream()
                .map(line -> line.getSections())
                .flatMap(sections -> sections.stream())
                .forEach(this::setEdgeWeight);

        dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    public List<Station> getShortestPath(Station source, Station target) {
        validateStation(source, target);
        return calculatePath(source, target);
    }

    public int getShortestDistance(Station source, Station target) {
        validateStation(source, target);
        return calculateDistance(source, target);
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
            throw new IllegalPathArgumentException(NOT_EQUALS_STATION);
        }

        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            throw new IllegalPathArgumentException(NOT_EXISTS_STATION);
        }
    }

    private List<Station> calculatePath(Station source, Station target) {
        GraphPath path = getPath(source, target);
        return path.getVertexList();
    }

    private GraphPath getPath(Station source, Station target) {
        GraphPath path = dijkstraShortestPath.getPath(source, target);
        validatePath(path);
        return path;
    }

    private void validatePath(GraphPath path) {
        if (path == null) {
            throw new IllegalPathArgumentException(NOT_EXISTS_STATION_EDGE);
        }
    }

    private int calculateDistance(Station source, Station target) {
        GraphPath path = getPath(source, target);
        return (int) path.getWeight();
    }
}
