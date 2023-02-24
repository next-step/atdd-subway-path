package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class LinePath {

    private List<Line> lines;

    public LinePath(List<Line> lines) {
        this.lines = lines;
    }

    public int getShortestDistance(Station source, Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = createPathGraph();
        return (int) new DijkstraShortestPath(graph).getPath(source, target).getWeight();
    }

    private WeightedMultigraph createPathGraph() {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        for (Line line : lines) {
            List<Section> sections = line.getAllSections();
            for (Section section : sections) {
                graph.addVertex(section.getUpStation());
                graph.addVertex(section.getDownStation());
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
            }
        }

        return graph;
    }


}
