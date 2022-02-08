package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class Path {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final GraphPath<Station, DefaultWeightedEdge> graphPath;

    public Path(List<Section> sections, Station startStation, Station arrivalStation) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        initGraph(sections);

        graphPath = new DijkstraShortestPath<>(graph).getPath(startStation, arrivalStation);
    }

    private void initGraph(List<Section> sections) {
        addVertexes(sections);
        addEdges(sections);
    }

    private void addEdges(List<Section> sections) {
        sections.forEach(section -> getEdgeWeight(graph, section));
    }

    private void addVertexes(List<Section> sections) {
        sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .forEach(graph::addVertex);
    }

    private void getEdgeWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
    }

    public List<Station> getVertexes() {
        return Collections.unmodifiableList(graphPath.getVertexList());
    }

    public int getWeight() {
        return (int) graphPath.getWeight();
    }
}
