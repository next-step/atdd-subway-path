package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {

	private final LineService lineService;
	private final PathFinder pathFinder;
	private final StationService stationService;

	public PathService(LineService lineService, PathFinder pathFinder, StationService stationService) {
		this.lineService = lineService;
		this.pathFinder = pathFinder;
		this.stationService = stationService;
	}

	public PathResponse getPath(Long source,  Long target) {
		Station sourceStation = stationService.findById(source);
		Station targetStation = stationService.findById(target);
		List<Line> lines = lineService.findAll();
		return pathFinder.find(lines, sourceStation, targetStation);
	}
}
