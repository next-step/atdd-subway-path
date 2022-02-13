package nextstep.subway.domain.util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LinePath {
	WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

	public LinePath(List<Line> lines) {
		lines.stream()
			.map(Line::getSections)
			.flatMap(Collection::stream)
			.forEach(it -> {
				graph.addVertex(it.getUpStation());
				graph.addVertex(it.getDownStation());

				graph.setEdgeWeight(graph.addEdge(it.getUpStation(), it.getDownStation()), it.getDistance());
			});
	}

	public PathResponse searchPath(Station source, Station target) {
		DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
		GraphPath path = dijkstraShortestPath.getPath(source, target);

		List<Station> vertexList = path.getVertexList();
		List<StationResponse> stations = vertexList.stream()
			.map(StationResponse::from)
			.collect(Collectors.toList());

		return PathResponse.of(stations, (int)path.getWeight());
	}
}
