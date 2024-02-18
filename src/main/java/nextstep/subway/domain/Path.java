package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public final class Path {
	private static WeightedMultigraph<Long, DefaultWeightedEdge> path = new WeightedMultigraph(DefaultWeightedEdge.class);
	private static DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath<>(path);

	public static void addPath(Long source, Long target, int weight) {
		path.addVertex(source);
		path.addVertex(target);

		path.setEdgeWeight(path.addEdge(source, target), weight);
	}

	public static List<Long> getPath(Long source, Long target) {
		return dijkstraShortestPath.getPath(source, target).getVertexList();
	}

	public static double getDistance(Long source, Long target) {
		return dijkstraShortestPath.getPathWeight(source, target);
	}

	public static void removePath(Long source, Long target) {
		path.removeEdge(source, target);
	}
}
