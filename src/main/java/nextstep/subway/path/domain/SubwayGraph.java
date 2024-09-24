package nextstep.subway.path.domain;

import java.util.List;
import nextstep.subway.section.domain.Section;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class SubwayGraph {

    public final WeightedMultigraph<Long, DefaultWeightedEdge> graph;

    public SubwayGraph(List<Section> sections) {
        this.graph = createGraph(sections);
    }

    public DijkstraShortestPath getShortestPath() {
        return new DijkstraShortestPath(graph);
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> createGraph(List<Section> sections) {
        final var graph =
                WeightedMultigraph.<Long, DefaultWeightedEdge>builder(DefaultWeightedEdge.class).build();

        sections.stream().forEach(
                section -> {
                    graph.addVertex(section.getUpStationId());
                    graph.addVertex(section.getDownStationId());

                    DefaultWeightedEdge edge = graph.addEdge(section.getUpStationId(), section.getDownStationId());
                    graph.setEdgeWeight(edge, section.getDistance());
                }
        );

        return graph;
    }
}
