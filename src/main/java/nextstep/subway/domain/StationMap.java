package nextstep.subway.domain;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class StationMap {
	private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

	public StationMap(
		WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
		this.graph = graph;
	}

	public StationMap(List<Line> lines) {
		this(new WeightedMultigraph(DefaultWeightedEdge.class));

		for (Line line : lines) {
			initVertices(line);
			initEdgeWeights(line);
		}
	}

	private void initVertices(Line line) {
		for (Station station : line.getStations()) {
			graph.addVertex(station);
		}
	}

	private void initEdgeWeights(Line line) {
		for (Section section : line.getSections()) {
			graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
		}
	}

	public boolean containsVertex(Station station) {
		return graph.containsVertex(station);
	}

	public WeightedMultigraph<Station, DefaultWeightedEdge> getGraph() {
		return graph;
	}
}
