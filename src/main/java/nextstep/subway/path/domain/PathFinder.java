package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    DijkstraShortestPath dijkstraShortestPath;

    public PathFinder(List<Line> lines) {
        graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        addAllVertex(lines);
        addAllEdgeWeight(lines);
        dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    private void addAllVertex(List<Line> lines) {
        for (Line line : lines) {
            for (Station station : line.getStations()) {
                graph.addVertex(station);
            }
        }
    }

    private void addAllEdgeWeight(List<Line> lines) {
        for (Line line : lines) {
            for (Section section : line.getSections()) {
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
            }
        }
    }


    public List<Station> getShortestPathList(Station source, Station target) {
      return dijkstraShortestPath.getPath(target, source).getVertexList();
    }

    public int getShortestDistance(Station source, Station target) {
        return (int) dijkstraShortestPath.getPath(target, source).getWeight();
    }

}
