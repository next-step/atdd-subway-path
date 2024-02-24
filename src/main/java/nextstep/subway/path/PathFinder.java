package nextstep.subway.path;

import nextstep.subway.exception.SubwayException;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Optional;

public class PathFinder {

    public Path findPath(List<Line> lines, Station source, Station target) {
        validateEqualsStation(source, target);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = createGraph(lines);
        validateStationExists(graph, source, target);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath path = Optional.ofNullable(dijkstraShortestPath.getPath(source, target))
                .orElseThrow(() -> new SubwayException("출발역과 도착역이 연결이 되어 있지 않습니다."));

        return new Path(path.getVertexList(), path.getWeight());
    }

    private void validateEqualsStation(Station source, Station target) {
        if (source.equals(target)) {
            throw new SubwayException("출발역과 도착역이 같습니다.");
        }
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createGraph(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        lines.stream()
                .flatMap(line -> line.getSections().stream())
                .distinct()
                .forEach(section -> {
                    Station upStation = section.getUpStation();
                    Station downStation = section.getDownStation();
                    graph.addVertex(downStation);
                    graph.addVertex(upStation);
                    graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
                });
        return graph;
    }

    private void validateStationExists(WeightedMultigraph<Station, DefaultWeightedEdge> graph,
                                       Station source, Station target) {
        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            throw new SubwayException("존재하지 않은 역입니다.");
        }
    }

}
