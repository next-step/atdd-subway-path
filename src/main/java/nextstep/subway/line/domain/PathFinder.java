package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.EqualStationException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathFinder {

    private List<Line> lines;

    public PathFinder(List<Line> lines) {
        this.lines = Collections.unmodifiableList(lines);
    }

    public StationGraph getPathInfo(Station source, Station target) {
        validateEqualStation(source, target);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        for (Line line : lines) {
            setGraph(graph, line);
        }

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath pathInfo = dijkstraShortestPath.getPath(source, target);
        return new StationGraph(pathInfo);
    }

    private void validateEqualStation(Station source, Station target) {
        if (source == target) {
            throw new EqualStationException();
        }
    }

    private void setGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Line line) {
        List<Section> sections = getSections(line);
        setVertex(graph, sections);
        setEdgeWeight(graph, sections);
    }

    private List<Section> getSections(Line line) {
        return line.getSections().getSections();
    }

    private void setVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        List<Station> stations = getStations(sections);

        for (Station station : stations) {
            graph.addVertex(station);
        }
    }

    private void setEdgeWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        for (Section section : sections) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }
    }

    private List<Station> getStations(List<Section> sections) {
        return sections.stream()
                .flatMap(s -> Stream.of(s.getUpStation(), s.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }
}
