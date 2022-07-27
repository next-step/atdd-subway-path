package nextstep.subway.domain;

import static nextstep.subway.common.exception.errorcode.BusinessErrorCode.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.util.ObjectUtils;

import nextstep.subway.common.exception.BusinessException;

public class PathFinder {

	private WeightedMultigraph<Station, DefaultWeightedEdge> graph;
	private DijkstraShortestPath dijkstraShortestPath;

	public PathFinder(List<Section> sectionList) {
		graph = new WeightedMultigraph(DefaultWeightedEdge.class);
		addVertex(graph, getStationList(sectionList));
		addEdgeWeight(graph, sectionList);
		dijkstraShortestPath = new DijkstraShortestPath(graph);
	}

	public List<Station> getShortestPath(Station sourStation, Station targetStation) {
		GraphPath graphPath = dijkstraShortestPath.getPath(sourStation, targetStation);
		if (ObjectUtils.isEmpty(graphPath)) {
			throw new BusinessException(INVALID_STATUS);
		}

		if (graphPath.getEdgeList().isEmpty()) {
			throw new BusinessException(INVALID_STATUS);
		}

		return graphPath.getVertexList();
	}

	public long getSumOfDistance(Station sourStation, Station targetStation) {
		return (long)dijkstraShortestPath.getPathWeight(sourStation, targetStation);
	}

	private List<Station> getStationList(List<Section> sectionList) {
		return sectionList.stream()
			.flatMap(section -> Arrays.asList(section.getUpStation(), section.getDownStation()).stream())
			.distinct()
			.collect(Collectors.toList());
	}

	private void addVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph,
		List<Station> stationList) {
		for (Station station : stationList) {
			graph.addVertex(station);
		}
	}

	private void addEdgeWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph,
		List<Section> sectionList) {

		sectionList.forEach(
			section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()),
				section.getDistance())
		);

	}
}
