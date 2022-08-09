package nextstep.subway.applicaion;

import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.ui.SubwayErrorCode;
import nextstep.subway.ui.SubwayException;

@Service
@RequiredArgsConstructor
public class PathService {
	private final LineRepository lineRepository;
	private final StationService stationService;

	public PathResponse findPath(Long sourceId, Long targetId) {
		if (sourceId.equals(targetId)) {
			throw new SubwayException(SubwayErrorCode.SAME_STATION);
		}
		WeightedMultigraph<Station, DefaultWeightedEdge> graph = lineGraph();

		Station source = stationService.findStation(sourceId);
		Station target = stationService.findStation(targetId);

		DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
		GraphPath shortestPath = dijkstraShortestPath.getPath(source, target);
		if (shortestPath == null) {
			throw new SubwayException(SubwayErrorCode.NOT_FOUND_PATHS);
		}
		return PathResponse.from(shortestPath);
	}

	private WeightedMultigraph<Station, DefaultWeightedEdge> lineGraph() {
		WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

		List<Line> lines = lineRepository.findAll();
		lines.forEach(line -> {
			graph.addVertex(line.getSections().getSections().get(0).getUpStation());
			mapLine(graph, line);

		});
		return graph;
	}

	private void mapLine(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Line line) {
		line.getSections().getSections().forEach(section -> mapSection(graph, section));
	}

	private void mapSection(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
		Station upStation = section.getUpStation();
		Station downStation = section.getDownStation();
		graph.addVertex(downStation);
		graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
	}
}
