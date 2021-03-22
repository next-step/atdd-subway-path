package nextstep.study;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class JgraphtTest {

    WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

    @BeforeEach
    void setUp(){
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.addVertex("v4");
        graph.addVertex("v5");
        graph.addVertex("v6");
        graph.addVertex("v7");
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 4);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);
        graph.setEdgeWeight(graph.addEdge("v2", "v4"), 1);
        graph.setEdgeWeight(graph.addEdge("v4", "v5"), 1);
        graph.setEdgeWeight(graph.addEdge("v5", "v3"), 1);
    }

    @Test
    public void getDijkstraShortestPath() {
        String source = "v3";
        String target = "v1";

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<String> shortestPath = dijkstraShortestPath.getPath(source, target).getVertexList();
        assertThat(shortestPath).containsExactlyElementsOf(Arrays.asList("v3", "v5", "v4", "v2", "v1"));
        assertThat(shortestPath.size()).isEqualTo(5);
    }

    @Test
    public void 서로_만날_수_없는_역() {
        graph.addVertex("v8");
        graph.addVertex("v9");
        graph.setEdgeWeight(graph.addEdge("v8", "v9"), 1);

        String source = "v1";
        String target = "v9";

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        assertThatThrownBy(() ->
                dijkstraShortestPath.getPath(source, target).getVertexList())
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void getKShortestPaths() {
        String source = "v3";
        String target = "v1";

        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 2);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);

        List<GraphPath> paths = new KShortestPaths(graph, 100).getPaths(source, target);

        assertThat(paths).hasSize(2);
        paths.stream()
                .forEach(it -> {
                    assertThat(it.getVertexList()).startsWith(source);
                    assertThat(it.getVertexList()).endsWith(target);
                });
    }
}