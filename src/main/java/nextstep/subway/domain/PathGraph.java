package nextstep.subway.domain;

import nextstep.subway.domain.exception.NotEnoughStationsException;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class PathGraph {

    public static final int MINIMUM_STATION_COUNT = 2;

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    private PathGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }

    public static PathGraph valueOf(List<Line> lines) {
        if (hasLessThanTwoStations(lines)) {
            throw new NotEnoughStationsException();
        }
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        initializeVertex(graph, findStations(lines));
        initializeEdgeWeight(graph, findSections(lines));
        return new PathGraph(graph);
    }

    private static boolean hasLessThanTwoStations(List<Line> lines) {
        return findStations(lines).size() < MINIMUM_STATION_COUNT;
    }

    private static List<Station> findStations(List<Line> lines) {
        return lines.stream()
                .map(Line::getStations)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }

    private static List<Section> findSections(List<Line> lines) {
        return lines.stream()
                .map(Line::getSections)
                .flatMap(List::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    private static void initializeVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Station> stations) {
        for (Station station : stations) {
            graph.addVertex(station);
        }
    }

    private static void initializeEdgeWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        for (Section section : sections) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance().toInt());
        }
    }

    public Path findShortPath(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException();
        }

        return null;
    }
}
