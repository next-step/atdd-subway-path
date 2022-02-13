package nextstep.subway.applicaion;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

@Service
@Transactional
public class PathService {
	LineRepository lineRepository;
	StationRepository stationRepository;

	public PathService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	public PathResponse searchPath(Long sourceId, Long targetId) {
		List<Line> lines = lineRepository.findAll();
		Station source = stationRepository.getById(sourceId);
		Station target = stationRepository.getById(targetId);

		WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

		lines.stream()
			.map(Line::getSections)
			.flatMap(Collection::stream)
			.forEach(it -> {
				graph.addVertex(it.getUpStation());
				graph.addVertex(it.getDownStation());

				graph.setEdgeWeight(graph.addEdge(it.getUpStation(), it.getDownStation()), it.getDistance());
			});

		DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
		GraphPath path = dijkstraShortestPath.getPath(source, target);

		List<Station> vertexList = path.getVertexList();
		List<StationResponse> stations = vertexList.stream()
			.map(StationResponse::from)
			.collect(Collectors.toList());

		return new PathResponse(stations, (int)path.getWeight());
	}
}

