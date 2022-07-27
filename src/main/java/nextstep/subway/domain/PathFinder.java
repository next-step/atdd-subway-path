package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

/**
 * @author a1101466 on 2022/07/27
 * @project subway
 * @description
 */
public class PathFinder {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);

    public GraphPath<Station, DefaultWeightedEdge> getShoresPath(Station sourceStation, Station targetStation){
        GraphPath<Station, DefaultWeightedEdge> shortestPath = dijkstraShortestPath.getPath(sourceStation, targetStation);
        return shortestPath;
    }

    public void addStationToVertex(List<Station> stations){
        stations.forEach(graph::addVertex);
    }

    public void addSectionEdgeWeight(List<Section> sections){
        sections.forEach(section ->
                graph.setEdgeWeight(
                        graph.addEdge(section.getUpStation()
                                , section.getDownStation())
                        ,section.getDistance()
                )
        );
    }
}
