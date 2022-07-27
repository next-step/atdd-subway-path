package nextstep.subway.domain;

import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathFinder {

	public static List<String> getShorPath(List<Line> lineList, long source, long target) {
		WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
		addVertex(graph, getStationList(lineList));
		addEdgeWeight(graph, getSectionList(lineList));

		DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
		List<String> shortestPath = dijkstraShortestPath
			.getPath(Long.toString(source), Long.toString(target)).getVertexList();

		return shortestPath;
	}

	private static List<Station> getStationList(List<Line> lineList) {
		return lineList.stream()
			.flatMap(line -> line.getStations().stream())
			.distinct()
			.collect(Collectors.toList());
	}

	private static List<Section> getSectionList(List<Line> lineList) {
		return lineList.stream()
			.flatMap(line -> line.getSections().stream())
			.collect(Collectors.toList());
	}

	private static void addVertex(WeightedMultigraph<String, DefaultWeightedEdge> graph, List<Station> stationList) {
		for (Station station : stationList) {
			graph.addVertex(station.getId().toString());
		}
	}

	private static void addEdgeWeight(WeightedMultigraph<String, DefaultWeightedEdge> graph,
		List<Section> sectionList) {
		for (Section section : sectionList) {
			graph.setEdgeWeight(graph.addEdge(
					section.getUpStation().getId().toString()
					, section.getDownStation().getId().toString())
				, section.getDistance().getDistance());
		}
	}
}
