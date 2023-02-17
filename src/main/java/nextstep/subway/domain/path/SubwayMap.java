package nextstep.subway.domain.path;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.sections.Sections;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;

public class SubwayMap {

    private final List<Line> lines;
    private final WeightedGraph<Station, DefaultWeightedEdge> map;

    public SubwayMap(List<Line> lines, WeightedGraph<Station, DefaultWeightedEdge> graph) {
        this.lines = lines;
        this.map = drawSubwayMap(graph);
    }

    private WeightedGraph drawSubwayMap(WeightedGraph<Station, DefaultWeightedEdge> graph) {
        registerVertex(graph);
        registerEdge(graph);
        return graph;
    }

    private void registerEdge(WeightedGraph<Station, DefaultWeightedEdge> graph) {
        lines.stream()
                .forEach(line -> {
                    Sections sections = line.getSections();
                    sections.getSections().stream()
                            .distinct()
                            .forEach(section -> {
                                DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
                                graph.setEdgeWeight(edge, section.getDistance().getValue());
                            });
                });
    }

    private void registerVertex(WeightedGraph<Station, DefaultWeightedEdge> graph) {
        lines.stream()
                .forEach(line -> {
                    List<Station> stations = line.getStations();
                    stations.stream()
                            .distinct()
                            .forEach(graph::addVertex);
                });
    }


    public WeightedGraph getMap() {
        return map;
    }

    public boolean hasStation(Station station) {
        return map.containsVertex(station);
    }
}
