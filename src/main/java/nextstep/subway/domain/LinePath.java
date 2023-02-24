package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.ArrayList;
import java.util.List;

/**
 * LinePath의 책임 ??
 * 출발지에서 목적지까지의 최단 거리를 계산 (Station, Section 정보를 기반으로 -> Line에 있는 정보)
 */
public class LinePath {

    private List<Line> lines = new ArrayList<>();

    public LinePath(List<Line> lines) {
        lines.addAll(lines);
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
