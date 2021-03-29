package nextstep.subway.path.domain;

import nextstep.subway.station.domain.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class SubwayGraph {

    WeightedMultigraph<Station, DefaultWeightedEdge> graph =
            new WeightedMultigraph<Station, DefaultWeightedEdge>(DefaultWeightedEdge.class);

    public WeightedMultigraph<Station, DefaultWeightedEdge> getSubwayGraph() {
        return graph;
    }

    public void addVertex(Station station) {
        graph.addVertex(station);
    }

    public DefaultWeightedEdge addEdge(Station upStation, Station downStation) {
        return graph.addEdge(upStation, downStation);
    }

    public void setEdgeWeight(DefaultWeightedEdge edge, double weight) {
        graph.setEdgeWeight(edge, weight);
    }
}
