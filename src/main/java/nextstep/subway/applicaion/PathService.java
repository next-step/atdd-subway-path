package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.exception.NotFoundPathException;
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

		if (depart.equals(arrival)) {
			throw new IllegalArgumentException("출발지와 도착지를 동일한 역으로 설정할 수 없습니다.");
		}

		GraphPath paths = pathFinder.findRoute(depart, arrival)
				.orElseThrow(() -> new NotFoundPathException(depart.getName(), arrival.getName()));

		return PathResponse.of(paths.getVertexList(), paths.getWeight());
	}
}
