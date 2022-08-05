package nextstep.subway.domain;

import lombok.Getter;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

@Getter
public class PathFinder {

    DijkstraShortestPath<Station, DefaultWeightedEdge> subwayGraph;

    public PathFinder() {

    }

    public PathFinder(List<Line> 노선목록) {
        this.subwayGraph = new DijkstraShortestPath<>(createSubwayGraph(노선목록));
    }

    public Path paths(Station startStation, Station endStation) {
        GraphPath<Station, DefaultWeightedEdge> paths = subwayGraph.getPath(startStation, endStation);
        return new Path(paths.getVertexList(), (int) paths.getWeight());
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createSubwayGraph(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> subwayGraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        lines.forEach(line -> createSectionGraph(subwayGraph, line.sections()));
        return subwayGraph;
    }

    private void createSectionGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        for (Section section : sections) {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }
    }

}
