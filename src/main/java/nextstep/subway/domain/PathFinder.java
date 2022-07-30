package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;

public class PathFinder {

	private WeightedMultigraph<Station, DefaultWeightedEdge> graph;
	private DijkstraShortestPath dijkstraShortestPath;

	public PathFinder(List<Line> lines) {
		graph = new WeightedMultigraph(DefaultWeightedEdge.class);
		init(lines);
		dijkstraShortestPath = new DijkstraShortestPath(graph);
	}

	public void init(List<Line> lines) {
		initVertex(lines);
		initEdgeWeight(lines);
	}

	public PathResponse findPath(Station departure, Station destination) {
		GraphPath path = getShortestPath(departure, destination);
		return new PathResponse(path.getVertexList(), (int) path.getWeight());
	}

	private void initVertex(List<Line> lines) {
		lines.stream()
				.map(Line::getStations)
				.flatMap(Collection::stream)
				.distinct()
				.forEach(graph::addVertex);
	}

	private void initEdgeWeight(List<Line> lines) {
		lines.stream()
				.map(Line::getSections)
				.flatMap(Collection::stream)
				.distinct()
				.forEach(section -> graph.setEdgeWeight(
						graph.addEdge(section.getUpStation(), section.getDownStation()),
						section.getDistance()));
	}

	private GraphPath getShortestPath(Station departure, Station destination) {
		return dijkstraShortestPath.getPath(departure, destination);
	}
}
