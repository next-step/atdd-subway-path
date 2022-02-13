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
import nextstep.subway.domain.util.LinePath;

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

		LinePath linePath = new LinePath(lines);
		List<StationResponse> stations = linePath.searchPath(source, target)
			.stream()
			.map(StationResponse::from)
			.collect(Collectors.toList());
		int distance = linePath.getWeight(source, target);

		return PathResponse.of(stations, distance);
	}
}

