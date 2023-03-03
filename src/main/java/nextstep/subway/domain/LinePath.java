package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class LinePath {

    private List<Line> lines;
    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public LinePath(List<Line> lines) {
        this.lines = lines;
        this.graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    }

    public int getShortestDistance(Station source, Station target) {
        return (int) createGraphPath(source, target).getWeight();
    }

    public List<Station> getStations(Station source, Station target) {
        return createGraphPath(source, target).getVertexList();
    }

    private GraphPath createGraphPath(Station source, Station target) {
        for (Line line : lines) {
            setGraphInfo(line.getAllSections());
        }

        return new DijkstraShortestPath(graph).getPath(source, target);
    }

    private void setGraphInfo(List<Section> sections) {
        for (Section section : sections) {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }
    }

}
