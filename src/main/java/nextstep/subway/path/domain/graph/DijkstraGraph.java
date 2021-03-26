package nextstep.subway.path.domain.graph;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DijkstraGraph implements Graph {

    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public DijkstraGraph() {
        graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    }

    @Override
    public Path getPath(List<Station> stations, List<Section> sections) {
        stations.forEach(graph::addVertex);
        sections.forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
        return new DijkstraPath(graph);
    }
}
