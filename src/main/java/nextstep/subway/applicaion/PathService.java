package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.ui.error.exception.BusinessException;
import nextstep.subway.ui.error.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;

import static nextstep.subway.ui.error.exception.ErrorCode.SAME_SOURCE_AND_TARGET;

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
		if (source.equals(target)) {
			throw new BusinessException(SAME_SOURCE_AND_TARGET);
		}
		Station sourceStation = stationService.findById(source);
		Station targetStation = stationService.findById(target);
		List<Line> lines = lineService.findAll();
		return pathFinder.find(lines, sourceStation, targetStation);
	}
}
