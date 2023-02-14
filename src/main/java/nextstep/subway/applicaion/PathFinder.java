package nextstep.subway.applicaion;

import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class PathFinder {

	private final GraphPath<Station, DefaultWeightedEdge> graphPath;

	public PathFinder(List<Line> lines, Station source, Station target) {
		this.graphPath = makeGraph(lines, source, target);
	}

	public SubwayPath findPath() {
		return new SubwayPath(this.graphPath.getVertexList(), this.graphPath.getWeight());
	}

	private GraphPath<Station, DefaultWeightedEdge> makeGraph(List<Line> lines, Station source, Station target) {
		DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = build(lines);
		GraphPath<Station, DefaultWeightedEdge> graphPath = dijkstraShortestPath.getPath(source, target);
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
