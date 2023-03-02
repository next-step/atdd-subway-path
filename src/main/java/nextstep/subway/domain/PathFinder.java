package nextstep.subway.domain;

import java.util.List;
import java.util.Objects;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.exception.NotExistedPathException;
import nextstep.subway.exception.NotExistedStationException;
import nextstep.subway.exception.SameStationException;

public class PathFinder {
	private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

	protected PathFinder(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
		this.graph = graph;
	}

	public PathFinder(List<Line> lines) {
		this(new WeightedMultigraph(DefaultWeightedEdge.class));

		for (Line line : lines) {
			initVertices(line);
		}
		for (Line line : lines) {
			initEdgeWeights(line);
		}
	}

	private void initVertices(Line line) {
		for (Station station : line.getStations()) {
			graph.addVertex(station);
		}
	}

	private void initEdgeWeights(Line line) {
		for (Section section : line.getSections().getSections()) {
			graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
		}
	}

	public GraphPath<Station, DefaultWeightedEdge> getShortestPath(Station source, Station target) {
		validateSourceAndTarget(source, target);
		validateExistStation(source, target);
		final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(
			graph);
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
		if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
			throw new NotExistedStationException();
		}
	}

	private void validateIsExistedPath(GraphPath<Station, DefaultWeightedEdge> shortestPath) {
		if (Objects.isNull(shortestPath)) {
			throw new NotExistedPathException();
		}
	}
}
