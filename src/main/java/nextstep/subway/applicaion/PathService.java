package nextstep.subway.applicaion;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.applicaion.dto.PathResponse;
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
		PathFinder pathFinder = new PathFinder(lineService.findAllLines());

		Station sourceStation = stationService.findById(sourceId);
		Station targetStation = stationService.findById(targetId);

		GraphPath<Station, DefaultWeightedEdge> shortestPath = pathFinder.getShortestPath(sourceStation, targetStation);
		return new PathResponse(shortestPath.getVertexList(), (int)shortestPath.getWeight());
	}
}
