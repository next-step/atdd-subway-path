package nextstep.subway.api.infrastructure.operators;

import java.util.List;

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

/**
 * @author : Rene Choi
 * @since : 2024/02/09
 */

@Component
public class DijkstraBasedShortestPathFinder implements PathFinder {

	@Override
	public Path findShortestPath(Station sourceStation, Station targetStation, List<Section> sections) {
		Graph<Station, DefaultWeightedEdge> graph = createGraph(sections);

		GraphPath<Station, DefaultWeightedEdge> shortestPath = new DijkstraShortestPath<>(graph).getPath(sourceStation, targetStation);

		return Path.of(fetchStationsInPath(shortestPath), calculateTotalDistance(graph, shortestPath));
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
