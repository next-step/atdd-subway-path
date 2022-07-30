package nextstep.subway.applicaion;

import org.springframework.stereotype.Service;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;

@Service
public class PathService {
	private final LineRepository lineRepository;
	private final StationService stationService;

	public PathService(LineRepository lineRepository, StationService stationService) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
	}

	public PathResponse searchShortestPath(Long source, Long target) {

		PathFinder pathFinder = PathFinder.of(lineRepository.findAll());
		return pathFinder.searchShortestPath(stationService.findById(source), stationService.findById(target));
	}
}
