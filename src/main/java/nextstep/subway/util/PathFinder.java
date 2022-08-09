package nextstep.subway.util;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
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
        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();

            graph.addVertex(upStation);
            graph.addVertex(downStation);
            graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
        }
    }

    private static List<Section> extractSections(List<Line> lines) {
        return lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}
