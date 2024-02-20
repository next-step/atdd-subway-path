package nextstep.subway.domain;

import nextstep.subway.entity.Section;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Objects;

public class PathFinder {
	private WeightedMultigraph<Long, DefaultWeightedEdge> weightedMultigraph;
	private DijkstraShortestPath dijkstraShortestPath;

	public PathFinder(List<Section> sections) {
		weightedMultigraph = new WeightedMultigraph(DefaultWeightedEdge.class);

		sections.forEach(this::addPath);

		dijkstraShortestPath = new DijkstraShortestPath<>(weightedMultigraph);
	}

	public List<Long> getPath(Long source, Long target) {
		GraphPath graphPath;

		try {
			graphPath = dijkstraShortestPath.getPath(source, target);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("경로에 존재하지 않는 역입니다.");
		}

		if(Objects.isNull(graphPath)) {
			throw new IllegalArgumentException("경로가 존재하지 않습니다.");
		}

		return graphPath.getVertexList();
	}

	public double getDistance(Long source, Long target) {
		return dijkstraShortestPath.getPathWeight(source, target);
	}

	private void addPath(Section section) {
		weightedMultigraph.addVertex(section.getDownStationId());
		weightedMultigraph.addVertex(section.getUpStationId());

		weightedMultigraph.setEdgeWeight(weightedMultigraph.addEdge(section.getDownStationId(), section.getUpStationId()), section.getDistance());
	}
}
