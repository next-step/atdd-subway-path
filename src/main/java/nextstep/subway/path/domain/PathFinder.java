package nextstep.subway.path.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.line.domain.Section;
import nextstep.subway.path.exception.NotExistPathException;
import nextstep.subway.path.exception.SameStationException;
import nextstep.subway.station.domain.Station;

public class PathFinder {

	private final List<Section> sections;
	private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

	private PathFinder(List<Section> sections) {
		this.sections = sections;
		this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
	}

	public static PathFinder of(List<Section> sections) {
		return new PathFinder(sections);
	}

	public List<Station> findShortestPath(Station source, Station target) {
		return getGraphPath(source, target).getVertexList();
	}

	public int findShortestPathDistance(Station source, Station target) {
		return (int) getGraphPath(source, target).getWeight();
	}

	private GraphPath<Station, DefaultWeightedEdge> getGraphPath(Station source, Station target) {
		validateFindBefore(source, target);

		final DijkstraShortestPath<Station, DefaultWeightedEdge> path = new DijkstraShortestPath<>(initializeGraph());
		final GraphPath<Station, DefaultWeightedEdge> graphPath = path.getPath(source, target);

		validateFindAfter(graphPath);
		return graphPath;
	}

	private WeightedMultigraph<Station, DefaultWeightedEdge> initializeGraph() {
		final Set<Station> stations = new HashSet<>();
		sections.forEach(section -> {
			stations.add(section.getUpStation());
			stations.add(section.getDownStation());
		});
		stations.forEach(graph::addVertex);
		sections.forEach(section ->
			graph.setEdgeWeight(
				graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance())
		);

		return graph;
	}

	private void validateFindAfter(GraphPath<Station, DefaultWeightedEdge> graphPath) {
		if (Objects.isNull(graphPath) || graphPath.getVertexList().isEmpty()) {
			throw new NotExistPathException();
		}
	}

	private void validateFindBefore(Station source, Station target) {
		if (source.equals(target)) {
			throw new SameStationException();
		}
	}
}
