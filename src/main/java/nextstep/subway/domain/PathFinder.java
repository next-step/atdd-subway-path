package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.exception.DisconnectedStationsException;
import nextstep.subway.exception.NotRegisteredStationException;
import nextstep.subway.exception.SameStationException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ErrorCode.*;

public class PathFinder {

	private WeightedMultigraph<Station, DefaultWeightedEdge> graph;
	private DijkstraShortestPath dijkstraShortestPath;
	private List<Line> lines = new ArrayList<>();

	public PathFinder(List<Line> lines) {
		graph = new WeightedMultigraph(DefaultWeightedEdge.class);
		init(lines);
		this.lines = lines;
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

		if (isNotRegistered(departure, destination)) {
			throw new NotRegisteredStationException(CANNOT_FIND_PATH_WITH_NOT_REGISTERED_STATION.getMessage());
		}

		GraphPath path = getShortestPath(departure, destination);

		if (isNotConnected(path)) {
			throw new DisconnectedStationsException(CANNOT_FIND_PATH_WITH_DISCONNECTED_STATIONS.getMessage());
		}

		return new PathResponse(path.getVertexList(), (int) path.getWeight());
	}

	private boolean isNotRegistered(Station departure, Station destination) {
		List<Station> stations = lines.stream()
				.map(Line::getStations)
				.flatMap(Collection::stream)
				.distinct()
				.collect(Collectors.toList());

		return stations.stream().noneMatch(station -> station.equals(departure)) ||
				stations.stream().noneMatch(station -> station.equals(destination));
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
