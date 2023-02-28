package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.SubwayPath;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class DijkstraPathFinder implements PathFinder {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> weightedMultigraph;

    public DijkstraPathFinder(WeightedMultigraph<Station, DefaultWeightedEdge> weightedMultigraph) {
        this.weightedMultigraph = weightedMultigraph;
    }

    @Override
    public SubwayPath findPath(List<Line> lines, Station sourceStation, Station targetStation) {
        validateStation(sourceStation, targetStation);
        addVertexAndEdge(lines);
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(weightedMultigraph);
        GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(sourceStation, targetStation);
        return getSubwayPath(path);
    }

    private void validateStation(Station sourceStation, Station targetStation) {
        if (sourceStation.equals(targetStation)) {
            throw new IllegalArgumentException("출발역과 도착역이 같은 경우 경로 조회를 할 수 없습니다.");
        }
    }

    private SubwayPath getSubwayPath(GraphPath<Station, DefaultWeightedEdge> path) {
        List<Station> stations = path.getVertexList();
        int totalDistance = (int) path.getWeight();
        return new SubwayPath(stations, totalDistance);
    }

    private void addVertexAndEdge(List<Line> lines) {
        addVertex(lines);
        addEdge(lines);
    }

    private void addVertex(List<Line> lines) {
        lines.stream()
                .flatMap(line -> line.getStations().stream())
                .collect(Collectors.toList())
                .forEach(weightedMultigraph::addVertex);
    }

    private void addEdge(List<Line> lines) {
        lines.stream()
                .flatMap(line -> line.getSections().stream())
                .collect(Collectors.toList())
                .forEach(this::addEdge);
    }

    private void addEdge(Section section) {
        DefaultWeightedEdge defaultWeightedEdge = weightedMultigraph.addEdge(section.getUpStation(), section.getDownStation());
        weightedMultigraph.setEdgeWeight(defaultWeightedEdge, section.getDistance());
    }
}
