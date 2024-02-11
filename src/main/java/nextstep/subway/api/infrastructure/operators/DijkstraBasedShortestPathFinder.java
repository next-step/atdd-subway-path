package nextstep.subway.api.infrastructure.operators;

import java.util.List;
import java.util.Optional;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.springframework.stereotype.Component;

import nextstep.subway.api.domain.model.entity.Section;
import nextstep.subway.api.domain.model.entity.Station;
import nextstep.subway.api.domain.model.vo.Path;
import nextstep.subway.api.domain.operators.PathFinder;
import nextstep.subway.common.exception.PathNotValidException;

/**
 * @author : Rene Choi
 * @since : 2024/02/09
 */

@Component
public class DijkstraBasedShortestPathFinder implements PathFinder {

	@Override
	public Path findShortestPath(Station sourceStation, Station targetStation, List<Section> sections) {
		Graph<Station, DefaultWeightedEdge> graph = createGraph(sections);

		GraphPath<Station, DefaultWeightedEdge> shortestPath = calculateShortestPath(sourceStation, targetStation, graph).orElseThrow(
			() -> new PathNotValidException("No path exists between the source and target stations."));

		return Path.of(fetchStationsInPath(shortestPath), calculateTotalDistance(graph, shortestPath));
	}

	private static Optional<GraphPath<Station, DefaultWeightedEdge>> calculateShortestPath(Station sourceStation, Station targetStation, Graph<Station, DefaultWeightedEdge> graph) {
		try {
			return Optional.ofNullable(new DijkstraShortestPath<>(graph).getPath(sourceStation, targetStation));
		} catch (Exception e) {
			throw new PathNotValidException("Shortest Path finding algorithm not supported");
		}
	}

	private Graph<Station, DefaultWeightedEdge> createGraph(List<Section> sections) {
		Graph<Station, DefaultWeightedEdge> graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		sections.forEach(section -> {
			graph.addVertex(section.getUpStation());
			graph.addVertex(section.getDownStation());
			Graphs.addEdgeWithVertices(graph, section.getUpStation(), section.getDownStation(), section.getDistance());
		});
		return graph;
	}

	private long calculateTotalDistance(Graph<Station, DefaultWeightedEdge> graph, GraphPath<Station, DefaultWeightedEdge> shortestPath) {
		return (long)shortestPath.getEdgeList().stream()
			.mapToDouble(graph::getEdgeWeight)
			.sum();
	}

	private List<Station> fetchStationsInPath(GraphPath<Station, DefaultWeightedEdge> shortestPath) {
		return shortestPath.getVertexList();
	}

}
