package subway.unit.path;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JgraphTest {
	private DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraShortestPath;

	@BeforeEach
	void beforeEach() {
		WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
		dijkstraShortestPath = new DijkstraShortestPath<>(graph);

		// 노드 설정
		graph.addVertex("v1");
		graph.addVertex("v2");
		graph.addVertex("v3");

		// 가중치를 더한 간선 설정
		// graph.addEdge() - 노드간 연결
		graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
		graph.setEdgeWeight(graph.addEdge("v2", "v3"), 3);
		graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);
	}

	@DisplayName("Jgraph 기본 방법을 익히기 - 노드간의 최단 경로 구하기")
	@Test
	void jGraphShortestPathTest() {
		// when
		List<String> shortestPath = dijkstraShortestPath.getPath("v3", "v1").getVertexList();

		// then
		List<String> expectedPath = List.of("v3", "v2", "v1");
		assertThat(shortestPath)
			.hasSize(3)
			.isEqualTo(expectedPath);
	}

	@DisplayName("Jgraph 기본 방법을 익히기 - 노드간의 최단 거리 구하기")
	@Test
	void jGraphShortestDistanceTest() {
		// when
		double pathWeight = dijkstraShortestPath.getPathWeight("v3", "v1");

		// then
		assertThat(pathWeight).isEqualTo(5);
	}
}
