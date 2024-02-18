package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Objects;

public final class Path {
	private static WeightedMultigraph<Long, DefaultWeightedEdge> path = new WeightedMultigraph(DefaultWeightedEdge.class);
	private static DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath<>(path);

	public static void addPath(Long source, Long target, int weight) {
		path.addVertex(source);
		path.addVertex(target);

		path.setEdgeWeight(path.addEdge(source, target), weight);
	}

	public static List<Long> getPath(Long source, Long target) {
		GraphPath graphPath;

		try {
			graphPath = dijkstraShortestPath.getPath(source, target);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("경로에 존재하지 않는 역입니다.");
		}

		if(Objects.isNull(graphPath)) {
			throw new IllegalArgumentException("경로가 존재하지 않습니다.");
		}

		return graphPath.getVertexList();
	}

	public static double getDistance(Long source, Long target) {
		return dijkstraShortestPath.getPathWeight(source, target);
	}

	public static void removePath(Long source, Long target) {
		path.removeEdge(source, target);
	}
}
