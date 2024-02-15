package nextstep.subway.paths;

import nextstep.subway.exception.HttpBadRequestException;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class PathFinder {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
    private DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = null;

    public PathFinder(List<Line> lines) {
        for (Line line : lines) {
            Set<Station> stations = new LinkedHashSet<>();
            line.getSections().getSections().forEach(section -> {
                stations.add(section.getUpStation());
                stations.add(section.getDownStation());
            });
            for (Station station : stations) {
                graph.addVertex(station);
            }

            line.getSections().getSections().forEach(section -> {
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
            });
        }
        dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    }

    public Path findPath(Station start, Station end) {
        validation(start, end);

        GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(start, end);
        if(path == null) {
            throw new HttpBadRequestException("경로가 존재하지 않습니다.");
        }
        List<Station> shortestPath = path.getVertexList();
        double pathWeight = dijkstraShortestPath.getPathWeight(start, end);
        return new Path(shortestPath, pathWeight);
    }

    private void validation(Station start, Station end) {
        if (start.equals(end)) {
            throw new HttpBadRequestException("출발역과 도착역이 같습니다.");
        }
        if (!graph.containsVertex(start) || !graph.containsVertex(end)) {
            throw new HttpBadRequestException("존재하지 않는 역입니다.");
        }
    }
}
