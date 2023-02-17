package nextstep.subway.applicaion;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.PathErrorCode;
import nextstep.subway.domain.exception.PathSearchException;

public class PathFinder {

	private final List<Line> lines;

	public PathFinder(List<Line> lines) {
		this.lines = new ArrayList<>(lines);
	}

	public SubwayPath findPath(Station source, Station target) {
		validateRequestStation(source, target);

		GraphPath<Station, DefaultWeightedEdge> graphPath = makeGraph(this.lines, source, target);
		return new SubwayPath(graphPath.getVertexList(), graphPath.getWeight());
	}

	private void validateRequestStation(Station source, Station target) {
		if (source == target) {
			throw new PathSearchException(PathErrorCode.EQUAL_SEARCH_STATION);
		}
	}

	private GraphPath<Station, DefaultWeightedEdge> makeGraph(List<Line> lines, Station source, Station target) {
		DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = build(lines);
		GraphPath<Station, DefaultWeightedEdge> graphPath = dijkstraShortestPath.getPath(source, target);

		if (graphPath == null) {
			throw new PathSearchException(PathErrorCode.NOT_CONNECTION);
		}

		return graphPath;
	}

	private DijkstraShortestPath<Station, DefaultWeightedEdge> build(List<Line> lines) {
		WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
		lines.forEach(line -> makePath(graph, line));
		return new DijkstraShortestPath<>(graph);
	}

	private void makePath(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Line line) {
		line.getSections()
			.forEach(section -> {
					addVertex(graph, section);
					addEdge(graph, section);
				}
			);
	}

	private void addVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
		graph.addVertex(section.getUpStation());
		graph.addVertex(section.getDownStation());
	}

	private void addEdge(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
		graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
	}
}
