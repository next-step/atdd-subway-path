package nextstep.subway.domain;

import lombok.Getter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

@Getter
public class StationWeightedMultigraph {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

    public void setGraph(List<Station> vertices, List<Section> edgeWeight) {
        addVertex(vertices);
        setEdgeWeight(edgeWeight);
    }

    private void addVertex(List<Station> stations) {
        for (Station station : stations) {
            graph.addVertex(station);
        }
    }

    private void setEdgeWeight(List<Section> sections) {
        for(Section section : sections) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistanceIntValue());
        }
    }
}
