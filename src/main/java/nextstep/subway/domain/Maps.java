package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import java.util.List;

public class Maps {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    private Maps(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }

    public static Maps of(List<Station> stations, List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        stations.forEach(graph::addVertex);
        sections.forEach(section
                -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation())
                , section.getDistance()));

        return new Maps(graph);

    }

    public Paths findShortestPath(Station source, Station target) {

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<Station> shortestPath = dijkstraShortestPath.getPath(source, target).getVertexList();
        double weight = dijkstraShortestPath.getPath(source, target).getWeight();
        return new Paths(shortestPath, (int) weight);
    }
}
