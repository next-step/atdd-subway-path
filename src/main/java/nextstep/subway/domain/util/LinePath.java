package nextstep.subway.domain.util;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.ExceptionCode;
import nextstep.subway.domain.exception.LinePathException;

public class LinePath {
	private Sections sections = new Sections();
	private WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);;

	public LinePath(List<Line> lines) {
		sections.addAll(getSections(lines));
		setGraph(lines);
	}

	public List<Station> searchPath(Station source, Station target) {
		validFilter(source, target);
		validExist(source, target);

		DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
		Optional<GraphPath> path = Optional.ofNullable(dijkstraShortestPath.getPath(source, target));

		return path
			.orElseThrow(() -> new LinePathException(ExceptionCode.NOT_FOUND_PATH.getMessage()))
			.getVertexList();
	}

	public int getWeight(Station source, Station target) {
		validFilter(source, target);
		validExist(source, target);

		DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
		Optional<GraphPath> path = Optional.ofNullable(dijkstraShortestPath.getPath(source, target));

		return (int)path
			.orElseThrow(() -> new LinePathException(ExceptionCode.NOT_FOUND_PATH.getMessage()))
			.getWeight();
	}

	private void validFilter(Station source, Station target) {
		if(source.equals(target)) {
			throw new IllegalArgumentException();
		}
	}

	private void validExist(Station source, Station target) {
		if(!sections.isContain(source) || !sections.isContain(target)) {
			throw new LinePathException(ExceptionCode.NO_ENTITY.getMessage());
		}
	}

	private List<Section> getSections(List<Line> lines) {
		return lines.stream()
			.map(Line::getSections)
			.flatMap(Collection::stream)
			.collect(Collectors.toList());
	}

	private void setGraph(List<Line> lines) {
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
}
