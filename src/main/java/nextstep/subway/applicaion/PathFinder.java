package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.ShortestPathResponse;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

	public static ShortestPathResponse findShortestPath(List<Line> lines, Station sourceStation, Station targetStation) {
		Multigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

		for (Section section : Line.getAllSections(lines)) {
			graph.addVertex(section.getUpStation());
			graph.addVertex(section.getDownStation());
			graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), toDouble(section.getDistance()));
		}

		GraphPath<Station, DefaultWeightedEdge> shortestPath = DijkstraShortestPath.findPathBetween(graph, sourceStation, targetStation);

		return new ShortestPathResponse(shortestPath.getVertexList(), toInteger(shortestPath.getWeight()));
	}

	private static int toInteger(double weight) {
		return Double.valueOf(weight).intValue();
	}

	private static double toDouble(Distance distance) {
		return distance.getDistance();
	}
}
