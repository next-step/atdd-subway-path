package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.ShortestPathResponse;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;

@Service
public class PathService {

	private final LineQueryService lineQueryService;
	private final StationService stationService;

	public PathService(LineQueryService lineQueryService, StationService stationService) {
		this.lineQueryService = lineQueryService;
		this.stationService = stationService;
	}

	public ShortestPathResponse findPath(long source, long target) {
		final Station sourceStation = stationService.findById(source);
		final Station targetStation = stationService.findById(target);

		return PathFinder.findShortestPath(lineQueryService.findAllLines(), sourceStation, targetStation);
	}
}
