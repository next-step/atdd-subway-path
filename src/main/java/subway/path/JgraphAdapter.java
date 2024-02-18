package subway.path;

import java.util.List;
import java.util.Optional;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import subway.line.Line;
import subway.line.Section;
import subway.station.Station;

public class JgraphAdapter {
	private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

	private JgraphAdapter(List<Line> lines) {
		this.dijkstraShortestPath = init(lines);
	}

	public static JgraphAdapter of(List<Line> lines) {
		return new JgraphAdapter(lines);
	}

	private DijkstraShortestPath<Station, DefaultWeightedEdge> init(List<Line> lines) {
		WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

		for (Line line : lines) {
			List<Station> sortedSections = line.getSortedStations();
			for (Station station : sortedSections) {
				graph.addVertex(station);
			}

			List<Section> sections = line.getSortedSections();
			for (Section section : sections) {
				DefaultWeightedEdge weightedEdge = graph.addEdge(section.getUpStation(), section.getDownStation());
				graph.setEdgeWeight(weightedEdge, section.getDistance());
			}
		}

		return new DijkstraShortestPath<>(graph);
	}

	public List<Station> getPath(Station sourceStation, Station targetStation) {
		GraphPath<Station, DefaultWeightedEdge> graphPath = dijkstraShortestPath.getPath(sourceStation, targetStation);
		return Optional.ofNullable(graphPath)
			.map(GraphPath::getVertexList)
			.orElseThrow(IllegalArgumentException::new);
	}

	public Integer getPathWeight(Station sourceStation, Station targetStation) {
		double pathWeight = dijkstraShortestPath.getPathWeight(sourceStation, targetStation);
		return (int)pathWeight;
	}
}
