package nextstep.subway.domain;

import nextstep.subway.applicaion.exception.NotFoundPathException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {
	private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
	private final DijkstraShortestPath dijkstraShortestPath;

	private PathFinder(List<Line> lines) {
		this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
		this.dijkstraShortestPath = new DijkstraShortestPath(graph);

		makeGraph(lines);
	}

	public static PathFinder from(List<Line> lines) {
		return new PathFinder(lines);
	}

	private void makeGraph(List<Line> lines) {
		lines.forEach(this::addVertex);
		lines.forEach(this::addEdgeWeight);
	}

	private void addEdgeWeight(Line line) {
		line.getSections().forEach(section -> {
			graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
		});
	}

	private void addVertex(Line line) {
		line.getStations().forEach(graph::addVertex);
	}

	public GraphPath findRoute(List<Line> lines, Station depart, Station arrival) {
		GraphPath paths = dijkstraShortestPath.getPath(depart, arrival);

		if (paths == null) {
			throw new NotFoundPathException(depart.getName(), arrival.getName());
		}

		return paths;
	}
}
