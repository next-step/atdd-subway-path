package nextstep.subway.applicaion;

import java.util.List;

import org.jgrapht.GraphPath;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.ui.SubwayErrorCode;
import nextstep.subway.ui.SubwayException;

@Service
@RequiredArgsConstructor
public class PathService {
	private final StationService stationService;
	private final LineService lineService;

	public PathResponse findPath(Long sourceId, Long targetId) {
		if (sourceId.equals(targetId)) {
			throw new SubwayException(SubwayErrorCode.SAME_STATION);
		}

		Station source = stationService.findStation(sourceId);
		Station target = stationService.findStation(targetId);

		PathFinder pathFinder = new PathFinder(lineService.findAll());
		GraphPath shortestPath = pathFinder.find(source, target);

		return PathResponse.from(shortestPath);
	}
}
