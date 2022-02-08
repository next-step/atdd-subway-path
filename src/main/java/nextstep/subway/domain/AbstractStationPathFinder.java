package nextstep.subway.domain;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

abstract class AbstractStationPathFinder <T> {
    private final WeightedMultigraph<String, DefaultWeightedEdge> graph;

    public AbstractStationPathFinder(Lines lines) {
        this.graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        initialize(lines);
    }

    private void initialize(Lines lines) {
        addAllVertex(lines.getAllStations());
        setEdgeWeights(lines.getAllSections());
    }

    private void addAllVertex(List<Station> stations) {
        List<String> stationIds = stations.stream()
            .map(it -> it.getId().toString())
            .collect(Collectors.toList());

        stationIds.forEach(this.graph::addVertex);
    }

    private void setEdgeWeights(List<Section> sections) {
        sections.forEach(section -> {
            String upStationName = stationIdToString(section.getUpStation());
            String downStationName = stationIdToString(section.getDownStation());
            DefaultWeightedEdge edge = graph.addEdge(upStationName, downStationName);
            this.graph.setEdgeWeight(edge, section.getDistance());
        });
    }

    private String stationIdToString(Station station) {
        return station.getId().toString();
    }

    protected WeightedMultigraph getGraph() {
        return this.graph;
    }

    public abstract T getPath(String source, String target);
}
