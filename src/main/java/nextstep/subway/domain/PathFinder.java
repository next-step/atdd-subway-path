package nextstep.subway.domain;

import java.util.Collection;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorCode;

public class PathFinder {
	private final WeightedMultigraph<Station, DefaultWeightedEdge> lineGraph;
	private final DijkstraShortestPath dijkstraShortestPath;

	private PathFinder(List<Line> lineList) {
		this.lineGraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
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
		validateSourceAndTarget(source, target);
		GraphPath graphPath = searchGraphPath(source, target);
		return PathResponse.of(
			StationResponse.fromList(graphPath.getVertexList()),
			(int)graphPath.getWeight()
		);
	}

	private void validateSourceAndTarget(Station source, Station target) {
		if (isSameSourceAndTarget(source, target)) {
			throw new BusinessException(ErrorCode.SAME_SOURCE_AND_TARGET);
		}
		if (!lineGraph.containsVertex(source) || !lineGraph.containsVertex(target)) {
			throw new BusinessException(ErrorCode.STATION_NOT_INCLUDE_PATH);
		}
		if (dijkstraShortestPath.getPath(source, target) == null) {
			throw new BusinessException(ErrorCode.STATION_NOT_INCLUDE_PATH);
		}
	}

	private boolean isSameSourceAndTarget(Station source, Station target) {
		return source.equals(target);
	}

	private GraphPath searchGraphPath(Station source, Station target) {
		return this.dijkstraShortestPath.getPath(source, target);
	}
}
