package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;

public class PathFinder {

    PathGraph pathGraph;
    DijkstraShortestPath dijkstraShortestPath;

    public PathFinder(List<Line> lines) {
        pathGraph = new PathGraph(lines);
        dijkstraShortestPath = new DijkstraShortestPath(pathGraph.getGraph());
    }

    public List<Station> getShortestPathList(Station source, Station target) {
        checkPathValidity(source, target);
        return getPath(target, source).getVertexList();
    }

    public int getShortestDistance(Station source, Station target) {
        checkPathValidity(source, target);
        return (int) getPath(source, target).getWeight();
    }

    private GraphPath<Station, DefaultWeightedEdge> getPath(Station source, Station target) {
        GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(target, source);
        checkUnconnectedPath(path);
        return path;
    }

    private void checkPathValidity(Station source, Station target) {
        if (source == target) {
            throw new RuntimeException("출발역과 도착역이 같음");
        }
        if (!pathGraph.containsStation(source) || !pathGraph.containsStation(target)) {
            throw new RuntimeException("등록되지 않은 출발/도착역");
        }
    }

    private void checkUnconnectedPath(GraphPath path) {
        if (path == null) {
            throw new RuntimeException("연결되지 않은 역입니다.");
        }
    }
}
