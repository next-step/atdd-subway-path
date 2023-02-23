package nextstep.subway.path;

import static nextstep.subway.common.SubwayErrorMsg.*;

import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationResponse;

public class PathFinder {

	private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

	public PathFinder(List<Station> stations, List<Section> sections) {
		WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
		stations.forEach(graph::addVertex);
		sections.forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
		dijkstraShortestPath = new DijkstraShortestPath<>(graph);
	}

	public PathResponse pathSearch(Station source, Station target) {
		if (source.equals(target)) {
			throw new IllegalArgumentException(PATH_SAME_STATION.isMessage());
		}

		GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(source, target);
		if (path == null) {
			throw new IllegalArgumentException(PATH_NO_CONNECT.isMessage());
		}

		List<Station> shorStationPath = path.getVertexList();
		double distance = path.getWeight();

		List<StationResponse> stationResponse = shorStationPath.stream()
			.map(StationResponse::new)
			.collect(Collectors.toList());
		return new PathResponse(stationResponse, distance);
	}
}
