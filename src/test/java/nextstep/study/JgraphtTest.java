package nextstep.study;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.Test;

class JgraphtTest {
    WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    String 노드1 = "노드1";
    String 노드2 = "노드2";
    String 노드3 = "노드3";
    String 노드4 = "노드4";
    String 노드5 = "노드5";

    public JgraphtTest() {
        graph.addVertex(노드1);
        graph.addVertex(노드2);
        graph.addVertex(노드3);
        graph.addVertex(노드4);
        graph.addVertex(노드5);
        graph.setEdgeWeight(graph.addEdge(노드1, 노드2), 2);
        graph.setEdgeWeight(graph.addEdge(노드2, 노드3), 2);
        graph.setEdgeWeight(graph.addEdge(노드1, 노드3), 100);
        graph.setEdgeWeight(graph.addEdge(노드4, 노드5), 15);
    }

    @Test
    void getDijkstraShortestPath() {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<String> shortestPath = dijkstraShortestPath.getPath(노드1, 노드3).getVertexList();

        assertThat(shortestPath.size()).isEqualTo(3);
    }

    @Test
    void getKShortestPaths() {
        List<GraphPath> paths = new KShortestPaths(graph, 100).getPaths(노드1, 노드3);

        assertThat(paths).hasSize(2);
        paths.stream()
            .forEach(it -> {
                assertThat(it.getVertexList()).startsWith(노드1);
                assertThat(it.getVertexList()).endsWith(노드3);
            });
    }

    @Test
    void 연결되어_있지_않은_경로_조회() {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);

        assertThatNullPointerException().isThrownBy(() -> dijkstraShortestPath.getPath(노드1, 노드4).getVertexList());
    }

    @Test
    void 없는_지점으로_경로_조회() {
        String 노드6 = "노드6";
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);

        assertThatIllegalArgumentException().isThrownBy(() -> dijkstraShortestPath.getPath(노드1, 노드6).getVertexList());
    }
}