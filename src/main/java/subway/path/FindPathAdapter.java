package subway.path;

import java.util.List;
import java.util.Optional;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import subway.line.Line;
import subway.station.Station;

public class FindPathAdapter {
	private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

	private FindPathAdapter(List<Line> lines) {
		this.dijkstraShortestPath = init(lines);
	}

	public static FindPathAdapter of(List<Line> lines) {
		return new FindPathAdapter(lines);
	}

	private DijkstraShortestPath<Station, DefaultWeightedEdge> init(List<Line> lines) {
		WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

		lines.forEach(
			line -> {
				line.getSortedStations().forEach(graph::addVertex);
				line.getSortedSections().forEach(
					section ->
						graph.setEdgeWeight(
							graph.addEdge(section.getUpStation(), section.getDownStation()),
							section.getDistance()
						)
				);
			}
		);

		return new DijkstraShortestPath<>(graph);
	}

	public List<Station> getPath(Station sourceStation, Station targetStation) {
		GraphPath<Station, DefaultWeightedEdge> graphPath = dijkstraShortestPath.getPath(sourceStation, targetStation);
		return Optional.ofNullable(graphPath)
			.map(GraphPath::getVertexList)
			.orElseThrow(() -> new IllegalArgumentException("경로를 찾을수 없습니다."));
	}

	public int getPathWeight(Station sourceStation, Station targetStation) {
		double pathWeight = dijkstraShortestPath.getPathWeight(sourceStation, targetStation);
		return (int)pathWeight;
	}
}
