package nextstep.subway.domain;

import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import lombok.RequiredArgsConstructor;
import nextstep.subway.ui.SubwayErrorCode;
import nextstep.subway.ui.SubwayException;

@RequiredArgsConstructor
public class PathFinder {
	private List<Line> lines;

	public PathFinder(List<Line> lines) {
		this.lines = lines;
	}

	public GraphPath find(Station source, Station target) {
		WeightedMultigraph<Station, DefaultWeightedEdge> graph = lineGraph();

		DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
		GraphPath shortestPath = dijkstraShortestPath.getPath(source, target);

		if (shortestPath == null) {
			throw new SubwayException(SubwayErrorCode.NOT_FOUND_PATHS);
		}
		return shortestPath;
	}

	private WeightedMultigraph<Station, DefaultWeightedEdge> lineGraph() {
		WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

		this.lines.forEach(line -> {
			graph.addVertex(line.getSections().getSections().get(0).getUpStation());
			mapLine(graph, line);

		});
		return graph;
	}

	private void mapLine(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Line line) {
		line.getSections().getSections().forEach(section -> mapSection(graph, section));
	}

	private void mapSection(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
		Station upStation = section.getUpStation();
		Station downStation = section.getDownStation();
		graph.addVertex(downStation);
		graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
	}
}
