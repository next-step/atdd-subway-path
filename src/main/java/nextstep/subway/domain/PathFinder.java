package nextstep.subway.domain;

import java.util.Collection;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;

public class PathFinder {
	private final WeightedMultigraph<Station, DefaultWeightedEdge> lineGraph;

	private final DijkstraShortestPath dijkstraShortestPath;

	private PathFinder(List<Line> lineList) {
		this.lineGraph = new WeightedMultigraph<Station, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		init(lineList);
		this.dijkstraShortestPath = new DijkstraShortestPath(lineGraph);
	}

	public static PathFinder of(List<Line> lineList) {
		return new PathFinder(lineList);
	}

	private void init(List<Line> lineList) {
		setVertex(lineList);
		setEdgeWeight(lineList);
	}

	private void setVertex(List<Line> lineList) {
		lineList.stream()
			.map(Line::getStations)
			.flatMap(Collection::stream)
			.distinct()
			.forEach(lineGraph::addVertex);
	}

	private void setEdgeWeight(List<Line> lineList) {
		lineList.stream()
			.map(Line::getSections)
			.flatMap(Collection::stream)
			.distinct()
			.forEach(section -> lineGraph.setEdgeWeight(
				lineGraph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()
			));
	}

	public PathResponse searchShortestPath(Station source, Station target) {
		GraphPath graphPath = searchGraphPath(source, target);

		return PathResponse.of(
			StationResponse.fromList(graphPath.getVertexList()),
			(int)graphPath.getWeight()
		);
	}

	private GraphPath searchGraphPath(Station source, Station target) {
		return this.dijkstraShortestPath.getPath(source, target);
	}
}
