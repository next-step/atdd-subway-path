package nextstep.subway.applicaion;

import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;

@Service
@Transactional(readOnly = true)
public class PathService {
	private final LineService lineService;
	private final StationService stationService;

	public PathService(LineService lineService, StationService stationService) {
		this.lineService = lineService;
		this.stationService = stationService;
	}

	public PathResponse getPath(Long sourceId, Long targetId) {
		final PathFinder pathFinder = new PathFinder(lineService.findAllLines());

		final Station sourceStation = stationService.findById(sourceId);
		final Station targetStation = stationService.findById(targetId);

		final GraphPath<Station, DefaultWeightedEdge> shortestPath = pathFinder.getShortestPath(sourceStation,
			targetStation);

		final List<StationResponse> stations = shortestPath.getVertexList()
			.stream()
			.map(station -> new StationResponse(station.getId(), station.getName()))
			.collect(Collectors.toList());

		return new PathResponse(stations, (int)shortestPath.getWeight());
	}
}
