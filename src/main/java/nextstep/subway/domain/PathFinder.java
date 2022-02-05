package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PathFinder {

    public PathResponse findPath(List<Line> lines,Station from, Station to){
        WeightedMultigraph<Station, DefaultWeightedEdge> graph
                = new WeightedMultigraph(DefaultWeightedEdge.class);

        for (Line line : lines) {
            createGraph(line,graph);
        }

        DijkstraShortestPath dijkstraShortestPath
                = new DijkstraShortestPath(graph);
        List<Station> shortestPath
                = dijkstraShortestPath.getPath(from, to).getVertexList();
        double pathWeight = dijkstraShortestPath.getPathWeight(from, to);
        return PathResponse.of(shortestPath,pathWeight);
    }

    private void createGraph(Line line, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        List<Section> sections = line.getSections().getSections();
        for (Section section : sections) {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }
    }
}
