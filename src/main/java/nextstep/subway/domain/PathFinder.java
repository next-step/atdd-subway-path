package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    public static PathResponse findPath(List<Line> lines, Station from, Station to) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph
                = new WeightedMultigraph(DefaultWeightedEdge.class);

        for (Line line : lines) {
            createGraph(line, graph);
        }

        DijkstraShortestPath dijkstraShortestPath
                = new DijkstraShortestPath(graph);
        List<Station> shortestPath
                = dijkstraShortestPath.getPath(from, to).getVertexList();
        double pathWeight = dijkstraShortestPath.getPathWeight(from, to);
        return PathResponse.of(shortestPath, (int) pathWeight);
    }

    private static void createGraph(Line line, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        List<Section> sections = line.getSections().getSections();
        for (Section section : sections) {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }
    }
}
