package nextstep.subway.domain;

import lombok.val;
import nextstep.subway.domain.object.Distance;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lines {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

    public Lines(List<Line> lines) {
        val sections = getSections(lines);
        initGraph(sections);
    }

    private void initGraph(List<List<Section>> sections) {
        sections.forEach(sectionList -> {
            addVertex(sectionList);
            setEdgeWeight(sectionList);
        });
    }

    private List<List<Section>> getSections(List<Line> lines) {
        return lines.stream()
                .map(Line::getSections)
                .collect(Collectors.toList());
    }

    private void addVertex(List<Section> sectionList) {
        sectionList.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .forEach(graph::addVertex);
    }

    private void setEdgeWeight(List<Section> sectionList) {
        sectionList.forEach(section -> {
            graph.setEdgeWeight(
                    graph.addEdge(section.getUpStation(), section.getDownStation()),
                    section.getDistanceValue()
            );
        });
    }

    public List<Station> getShortestPath(Station sourceStation, Station targetStation) {
        val shortestPath = getDijkstraShortestPath(sourceStation, targetStation);
        return Collections.unmodifiableList(shortestPath.getVertexList());
    }

    public Distance getShortestPathDistance(Station sourceStation, Station targetStation) {
        val shortestPath = getDijkstraShortestPath(sourceStation, targetStation);
        return new Distance((int) shortestPath.getWeight());
    }

    private GraphPath<Station, DefaultWeightedEdge> getDijkstraShortestPath(Station sourceStation, Station targetStation) {
        validateStation(graph, sourceStation, targetStation);

        return new DijkstraShortestPath<>(graph)
                .getPath(sourceStation, targetStation);
    }

    private void validateStation(
            WeightedMultigraph<Station, DefaultWeightedEdge> graph,
            Station source,
            Station target
    ) {
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException();
        }

        if (!graph.containsVertex(target)) {
            throw new IllegalArgumentException();
        }

        if (source.equals(target)) {
            throw new IllegalArgumentException();
        }
    }
}
