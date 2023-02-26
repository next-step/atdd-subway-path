package nextstep.subway.domain;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;

public class graphMaker {

    public static WeightedMultigraph<Station, DefaultWeightedEdge> makeSubwayGraph(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        lines.stream()
                .map(line -> line.getSections().getSections())
                .flatMap(Collection::stream)
                .forEach(section -> create(graph, section));
        return graph;
    }

    private static void create(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        int distance = section.getDistance();
        graph.addVertex(upStation);
        graph.addVertex(downStation);
        graph.setEdgeWeight(graph.addEdge(upStation, downStation), distance);
    }
}
