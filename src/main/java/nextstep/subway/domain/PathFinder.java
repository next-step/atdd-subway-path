package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author a1101466 on 2022/07/27
 * @project subway
 * @description
 */
public class PathFinder {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);

    public PathFinder(List<Line> lines) {
        addStationToVertex(lines);
        addSectionEdgeWeight(lines);
    }

    public PathFinder() {
    }

    public GraphPath<Station, DefaultWeightedEdge> getShoresPath(Station sourceStation, Station targetStation){
        GraphPath<Station, DefaultWeightedEdge> shortestPath = dijkstraShortestPath.getPath(sourceStation, targetStation);
        return shortestPath;
    }

    public void addStationToVertex(List<Line> lines){
        lines.stream()
                .map(Line::getStations)
                .flatMap(List::stream)
                .forEach(graph::addVertex);
    }

    public void addSectionEdgeWeight(List<Line> lines){
        lines.stream()
                .map(Line::getSections)
                .flatMap(List::stream)
                .collect(Collectors.toList())
                .forEach(section ->
                        graph.setEdgeWeight(
                                graph.addEdge(section.getUpStation()
                                        , section.getDownStation())
                                ,section.getDistance()
                        )
                );
    }
}
