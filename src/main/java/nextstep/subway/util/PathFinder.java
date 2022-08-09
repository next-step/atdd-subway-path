package nextstep.subway.util;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathFinder {
    public static PathResponse findPath(List<Line> lines, Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException();
        }

        WeightedMultigraph<Station, DefaultWeightedEdge> subwayMap = new WeightedMultigraph(DefaultWeightedEdge.class);
        registerSections(subwayMap, extractSections(lines));
        return findShortestPath(source, target, subwayMap);
    }

    private static PathResponse findShortestPath(Station source, Station target, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        GraphPath path = new DijkstraShortestPath(graph).getPath(source, target);

        if (path == null) {
            throw new IllegalArgumentException();
        }

        return PathResponse.from(path);
    }

    private static void registerSections(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .forEach(graph::addVertex);

        sections.forEach(section -> graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance())
        );
    }

    private static List<Section> extractSections(List<Line> lines) {
        return lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}
