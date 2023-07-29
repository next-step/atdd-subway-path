package nextstep.subway.application;

import org.springframework.stereotype.Service;

import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.PathRequest;
import nextstep.subway.dto.PathResponse;

@Service
public class PathService {

	private final LineRepository lineRepository;
	private final StationService stationService;

	public PathService(LineRepository lineRepository, StationService stationService) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
	}

	public PathResponse findPath(PathRequest pathRequest) {
		Station source = stationService.findById(pathRequest.getSource());
		Station target = stationService.findById(pathRequest.getTarget());
		PathFinder pathFinder = new PathFinder(lineRepository.findAll());
		return PathResponse.from(pathFinder.findPath(source, target));
	}
}
