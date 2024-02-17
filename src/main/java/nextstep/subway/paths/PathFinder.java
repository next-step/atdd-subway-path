package nextstep.subway.paths;

import nextstep.subway.line.Line;
import nextstep.subway.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathFinder {

    public Path findPath(List<Line> lines, Station start, Station end) {

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        addStationsToGraph(lines, graph);
        addWeightToGraph(lines, graph);

        validation(start, end, graph);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(start, end);

        if(path == null) {
            throw new IllegalArgumentException("경로가 존재하지 않습니다.");
        }

        List<Station> shortestPath = path.getVertexList();
        double pathWeight = dijkstraShortestPath.getPathWeight(start, end);

        return new Path(shortestPath, pathWeight);
    }

    private void addWeightToGraph(List<Line> lines, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lines.stream().flatMap(it -> it.getSections().getSections().stream())
                .forEach(it -> graph.setEdgeWeight(graph.addEdge(it.getUpStation(), it.getDownStation()), it.getDistance()));
    }

    private void addStationsToGraph(List<Line> lines, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lines.stream().flatMap(it -> it.getStations().stream())
                .distinct()
                .collect(Collectors.toList())
                .forEach(graph::addVertex);
    }

    private void validation(Station start, Station end, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        if (start.equals(end)) {
            throw new IllegalArgumentException("출발역과 도착역이 같습니다.");
        }
        if (!graph.containsVertex(start) || !graph.containsVertex(end)) {
            throw new IllegalArgumentException("존재하지 않는 역입니다.");
        }
    }
}
