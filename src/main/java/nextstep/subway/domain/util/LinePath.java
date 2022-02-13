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
import nextstep.subway.exception.LinePathException;
import nextstep.subway.exception.ExceptionCode;

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

	public List<Station> searchPath(Station source, Station target) {
		validFilter(source, target);

		try {
			DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
			GraphPath path = dijkstraShortestPath.getPath(source, target);

			return path.getVertexList();
		}catch (NullPointerException e) {
			throw new LinePathException(ExceptionCode.NOT_FOUND_PATH.getMessage());
		}catch (IllegalArgumentException e) {
			throw new LinePathException(ExceptionCode.NO_ENTITY.getMessage());
		}
	}

	public int getWeight(Station source, Station target) {
		validFilter(source, target);

		try {
			DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
			GraphPath path = dijkstraShortestPath.getPath(source, target);

			return (int)path.getWeight();
		}catch (NullPointerException e) {
			throw new LinePathException(ExceptionCode.NOT_FOUND_PATH.getMessage());
		}catch (IllegalArgumentException e) {
			throw new LinePathException(ExceptionCode.NO_ENTITY.getMessage());
		}
	}

	private void validFilter(Station source, Station target) {
		if(source.equals(target)) {
			throw new IllegalArgumentException();
		}
	}
}
