package nextstep.subway.domain;

import java.util.List;
import java.util.Objects;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import nextstep.subway.exception.NotExistedPathException;
import nextstep.subway.exception.NotExistedStationException;
import nextstep.subway.exception.SameStationException;

public class PathFinder {
	private final StationMap stationMap;
	private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

	protected PathFinder(StationMap stationMap,
		DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath) {
		this.stationMap = stationMap;
		this.dijkstraShortestPath = dijkstraShortestPath;
	}

	public static PathFinder of(List<Line> lines) {
		StationMap stationMap = new StationMap(lines);
		DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(
			stationMap.getGraph());

		return new PathFinder(stationMap, dijkstraShortestPath);
	}

	public GraphPath<Station, DefaultWeightedEdge> getShortestPath(Station source, Station target) {
		validateSourceAndTarget(source, target);
		validateExistStation(source, target);

		final GraphPath<Station, DefaultWeightedEdge> shortestPath = dijkstraShortestPath.getPath(source, target);

		validateIsExistedPath(shortestPath);

		return shortestPath;
	}

	private void validateSourceAndTarget(Station source, Station target) {
		if (source.equals(target)) {
			throw new SameStationException();
		}
	}

	private void validateExistStation(Station source, Station target) {
		if (!stationMap.containsVertex(source) || !stationMap.containsVertex(target)) {
			throw new NotExistedStationException();
		}
	}

	private void validateIsExistedPath(GraphPath<Station, DefaultWeightedEdge> shortestPath) {
		if (Objects.isNull(shortestPath)) {
			throw new NotExistedPathException();
		}
	}
}
