package subway.path;

import java.util.List;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import subway.dto.path.PathResponse;
import subway.line.Line;
import subway.line.LineRepository;
import subway.line.Section;
import subway.station.Station;

@Transactional(readOnly = true)
@Service
public class PathService {
	private final LineRepository lineRepository;

	public PathService(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	public PathResponse findShortestPath(Station sourceStation, Station targetStation) {

		// Station station1 = new Station(7L, "교대역");
		// Station station2 = new Station(8L, "남부터미널역");
		// Station station3 = new Station(2L, "양재역");
		// List<Station> result = List.of(station1, station2, station3);

		WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
		List<Line> lines = lineRepository.findAll();
		for (Line line : lines) {
			List<Station> sortedSections = line.getSortedStations();
			for (Station station : sortedSections) {
				graph.addVertex(station);
			}

			List<Section> sections = line.getSortedSections();
			for (Section section : sections) {
				graph.setEdgeWeight(
					graph.addEdge(
						section.getUpStation(),
						section.getDownStation()
					),
					section.getDistance()
				);
			}
		}

		DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
		List<Station> vertexList = dijkstraShortestPath.getPath(sourceStation, targetStation).getVertexList();
		Double pathWeight = dijkstraShortestPath.getPathWeight(sourceStation, targetStation);
		return new PathResponse(vertexList, pathWeight.intValue());
	}
}
