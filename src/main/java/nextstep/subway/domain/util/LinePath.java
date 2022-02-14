package nextstep.subway.domain.util;

import java.util.Collection;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.ExceptionCode;
import nextstep.subway.domain.exception.LinePathException;

public class LinePath {
	WeightedMultigraph<Station, DefaultWeightedEdge> graph;

	public LinePath(List<Line> lines) {
		graph = new WeightedMultigraph(DefaultWeightedEdge.class);
		lines.stream()
			.map(Line::getSections)
			.flatMap(Collection::stream)
			.forEach(it -> {
				Station upStation = it.getUpStation();
				Station downStation = it.getDownStation();

				graph.addVertex(upStation);
				graph.addVertex(downStation);

				graph.setEdgeWeight(graph.addEdge(upStation, downStation), it.getDistance());
			});
	}

	public List<Station> searchPath(Station source, Station target) {
		validFilter(source, target);

		try {
			DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
			List<Station> stations = dijkstraShortestPath.getPath(source, target).getVertexList();

			return stations;
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
