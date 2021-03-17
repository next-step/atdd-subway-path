package nextstep.subway.path.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.line.domain.Section;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.exception.NotExistPathException;
import nextstep.subway.path.exception.SameStationException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

public class PathFinder {

	private final List<Section> sections;
	private final Set<Station> stations = new HashSet<>();
	private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

	private PathFinder(List<Section> sections) {
		this.sections = sections;
		sections.forEach(section -> {
			stations.add(section.getUpStation());
		 	stations.add(section.getDownStation());
		});
		this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
	}

	public static PathFinder of(List<Section> sections) {
		return new PathFinder(sections);
	}

	public PathResponse findShortestPath(Station source, Station target) {
		validateFindBefore(source, target);

		stations.forEach(graph::addVertex);
		sections.forEach(section ->
			graph.setEdgeWeight(
				graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance())
		);

		final DijkstraShortestPath<Station, DefaultWeightedEdge> path = new DijkstraShortestPath<>(graph);
		final GraphPath<Station, DefaultWeightedEdge> graphPath = path.getPath(source, target);

		validateFindAfter(graphPath);

		final List<Station> pathStations = graphPath.getVertexList();

		return PathResponse.of(
			pathStations.stream()
					.map(StationResponse::of)
					.collect(Collectors.toList()),
			(int) graphPath.getWeight()
		);
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
