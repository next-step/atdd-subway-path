package nextstep.subway.domain;

import nextstep.subway.domain.exception.CannotFindPathException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Path {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final GraphPath<Station, DefaultWeightedEdge> graphPath;

    public Path(List<Section> sections, Station startStation, Station arrivalStation) {
        validateStations(startStation, arrivalStation);
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        initGraph(sections);

        graphPath = new DijkstraShortestPath<>(graph).getPath(startStation, arrivalStation);
    }

    private void validateStations(Station startStation, Station arrivalStation) {
        if (Objects.isNull(arrivalStation)) {
            throw CannotFindPathException.notExistArrivalStation();
        }
        if (Objects.isNull(startStation)) {
            throw CannotFindPathException.notExistStartStation();
        }
        if (startStation.equals(arrivalStation)) {
            throw CannotFindPathException.sameStations();
        }
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
        DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
        graph.setEdgeWeight(edge, section.getDistance());
    }

    public List<Station> getVertexes() {
        validateReach();
        return Collections.unmodifiableList(graphPath.getVertexList());
    }

    public int getWeight() {
        validateReach();
        return (int) graphPath.getWeight();
    }

    private void validateReach() {
        if (Objects.isNull(graphPath)) {
            throw CannotFindPathException.cannotReach();
        }
    }
}
