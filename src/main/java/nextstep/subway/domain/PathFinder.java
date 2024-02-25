package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
                .flatMap(line -> line.getSections().getOrderedStations().stream())
                .distinct()
                .forEach(station -> {
                    graph.addVertex(station);
                    System.out.println("1111 "+station.getName());
                });

//        lineList.stream()
//                .flatMap(line -> line.getSections().getSections().stream())
//                .forEach(section -> {
//                    System.out.println("upStation - " + section.getUpStation().getName() + " downStation -" + section.getDownStation().getName());
//                    graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
//                });

        lineList.forEach(line -> {
            List<Section> sections = line.getSections().getSections();
            sections.forEach(section -> {
                System.out.println("upStation - " + section.getUpStation().getName() + " downStation -" + section.getDownStation().getName());
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
            });
        });

        return graph;
    }
}
