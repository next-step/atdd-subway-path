package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.exception.SameStationException;
import nextstep.subway.exception.DisconnectedStationsException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;

import static nextstep.subway.exception.ErrorCode.CANNOT_FIND_PATH_WITH_DISCONNECTED_STATIONS;
import static nextstep.subway.exception.ErrorCode.CANNOT_FIND_PATH_WITH_SAME_STATION;

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
		if (isSameStation(departure, destination)) {
			throw new SameStationException(CANNOT_FIND_PATH_WITH_SAME_STATION.getMessage());
		}

		GraphPath path = getShortestPath(departure, destination);

		if (isNotConnected(path)) {
			throw new DisconnectedStationsException(CANNOT_FIND_PATH_WITH_DISCONNECTED_STATIONS.getMessage());
		}

		return new PathResponse(path.getVertexList(), (int) path.getWeight());
	}

	private boolean isNotConnected(GraphPath path) {
		return path == null;
	}

	private boolean isSameStation(Station departure, Station destination) {
		return departure.equals(destination);
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
