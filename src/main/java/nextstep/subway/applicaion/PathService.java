package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PathService {
	private final LineRepository lineRepository;
	private final StationService stationService;

	public PathService(LineRepository lineRepository, StationService stationService) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
	}

	public PathResponse findRoute(Long departId, Long arrivalId) {
		PathFinder pathFinder = PathFinder.from(lineRepository.findAll());

		Station depart = stationService.findById(departId);
		Station arrival = stationService.findById(arrivalId);

		GraphPath paths = pathFinder.findRoute(depart, arrival);

		return PathResponse.of(paths.getVertexList(), paths.getWeight());
	}
}
