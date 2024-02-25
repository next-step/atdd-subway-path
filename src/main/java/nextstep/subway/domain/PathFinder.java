package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;


public class PathFinder {
    public Path findPath(List<Line> lineList, Station source, Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = createGraph(lineList);

        DijkstraShortestPath<Station, DefaultWeightedEdge> shortestPath = new DijkstraShortestPath<>(graph);
        List<Station> vertexList = shortestPath.getPath(source, target).getVertexList();
        double distance = shortestPath.getPathWeight(source, target);

        return Path.builder().path(vertexList).distance(distance).build();
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createGraph(List<Line> lineList) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        lineList.stream()
                .flatMap(line -> line.getSections().getSections().stream())
                .distinct()
                .forEach(section -> {
                    graph.addVertex(section.getUpStation());
                    graph.addVertex(section.getDownStation());
                    graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
                });

        return graph;
    }
}
