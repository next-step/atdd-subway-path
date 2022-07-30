package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.NotRegisteredStationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PathService {

	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public PathResponse findPath(long source, long target) {
		Station departure = findStation(source);
		Station destination = findStation(target);
		PathFinder pathFinder = new PathFinder(lineRepository.findAll());
		return pathFinder.findPath(departure, destination);
	}

	private Station findStation(long stationId) {
		return stationRepository.findById(stationId)
				.orElseThrow(
						() -> new NotRegisteredStationException(ErrorCode.CANNOT_FIND_PATH_WITH_NOT_REGISTERED_STATION.getMessage())
				);
	}
}
