package nextstep.subway.line.domain;

import nextstep.subway.line.exception.GraphNullException;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SubwayMap {

    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public SubwayMap(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        validateGraph(graph);
        this.graph = graph;
    }

    private void validateGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        if (Objects.isNull(graph)) {
            throw new GraphNullException();
        }
    }

    public GraphPath getPath(List<Line> lines, Station source, Station target) {
        for (Line line : lines) {
            setGraph(graph, line);
        }

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        return dijkstraShortestPath.getPath(source,target);
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
