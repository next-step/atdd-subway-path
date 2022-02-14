package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Optional;

public class PathFinder {
	private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
	private final DijkstraShortestPath dijkstraShortestPath;

	private PathFinder(List<Line> lines) {
		this.graph = new WeightedMultigraph<Station, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.dijkstraShortestPath = new DijkstraShortestPath(graph);

		makeGraph(lines);
	}

	public static PathFinder from(List<Line> lines) {
		return new PathFinder(lines);
	}

	private void makeGraph(List<Line> lines) {
		lines.stream().forEach(this::addVertex);
		lines.stream().forEach(this::addEdgeWeight);
	}

	private void addEdgeWeight(Line line) {
		line.getSections().stream()
				.forEach(section -> {
					graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
				});
	}

	private void addVertex(Line line) {
		line.getStations().stream()
				.forEach(graph::addVertex);
	}

	public Optional<GraphPath> findRoute(Station depart, Station arrival) {
		return Optional.ofNullable(dijkstraShortestPath.getPath(depart, arrival));
	}
}
