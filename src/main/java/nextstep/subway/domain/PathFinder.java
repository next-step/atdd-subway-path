package nextstep.subway.domain;

import java.util.Set;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathFinder {

    private WeightedMultigraph<String, DefaultWeightedEdge> graph;

    public PathFinder(Set<SectionEdge> edges) {
        this.graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        edges.forEach(edge -> {
            String source = edge.getSource();
            String target = edge.getTarget();
            graph.addVertex(source);
            graph.addVertex(target);
            int weight = edge.getWeight();
            graph.setEdgeWeight(graph.addEdge(source, target), weight);
        });
    }

    public Path findPath(String source, String target) {
        return new Path(this.graph, source, target);
    }
}
