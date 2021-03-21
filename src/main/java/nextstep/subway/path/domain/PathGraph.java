package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathGraph {

    WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathGraph(List<Line> lines) {
        graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        addAllVertex(lines);
        addAllEdgeWeight(lines);
    }

    private void addAllVertex(List<Line> lines) {
        for (Line line : lines) {
            for (Station station : line.getStations()) {
                graph.addVertex(station);
            }
        }
    }

    private void addAllEdgeWeight(List<Line> lines) {
        for (Line line : lines) {
            for (Section section : line.getSections()) {
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
            }
        }
    }

    public WeightedMultigraph<Station, DefaultWeightedEdge> getGraph() {
        return graph;
    }

    public boolean containsStation(Station station) {
        return getGraph().containsVertex(station);
    }

}
