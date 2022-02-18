package nextstep.subway.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathFinder {
    private WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

    private PathFinder() {
    }

    public static PathFinder from(List<Line> lines) {
        PathFinder pathFinder = new PathFinder();
        pathFinder.init(lines);
        return pathFinder;
    }

    public void init(List<Line> lines) {
        lines.stream()
            .map(Line::getSections)
            .map(Sections::getSectionList)
            .flatMap(Collection::stream)
            .forEach(
                section -> {
                    Station upStation = section.getUpStation();
                    Station downStation = section.getDownStation();
                    graph.addVertex(upStation);
                    graph.addVertex(downStation);
                    graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
                }
            );
    }

    public Path searchPath(Station source, Station target) {
        validationSearchPathParams(source, target);
        GraphPath<Station, DefaultWeightedEdge> shortestPath = searchShortestPath(source, target);

        List<Station> stations = shortestPath.getVertexList();
        int distance = (int) shortestPath.getWeight();
        return new Path(stations, distance);
    }

    public GraphPath<Station, DefaultWeightedEdge> searchShortestPath(Station source, Station target) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> graphPath = dijkstraShortestPath.getPath(source, target);

        return Optional.ofNullable(graphPath)
            .orElseThrow(() -> {
                throw new IllegalArgumentException("출발역과 도착역이 연결되어 있지 않습니다.");
            });
    }

    private void validationSearchPathParams(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역이 동일합니다.");
        }

        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            throw new IllegalArgumentException("노선에 포함된 역의 경로만 조회 가능합니다.");
        }
    }
}
