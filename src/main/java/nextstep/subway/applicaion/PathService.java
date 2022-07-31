package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.NotRegisteredStationException;
import org.jgrapht.GraphPath;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PathService {

	private final LineRepository lineRepository;
	private final StationRepository stationRepository;
	private final StationService stationService;

	public PathResponse findPath(long source, long target) {
		Station departure = findStation(source);
		Station destination = findStation(target);
		PathFinder pathFinder = new PathFinder(lineRepository.findAll());
		GraphPath path = pathFinder.findPath(departure, destination);

		return new PathResponse(createStationResponse(path), (int) path.getWeight());
	}

	private List<StationResponse> createStationResponse(GraphPath path) {
		List<Station> stations = path.getVertexList();
		return stations.stream()
				.map(stationService::createStationResponse)
				.collect(Collectors.toList());
	}

	private Station findStation(long stationId) {
		return stationRepository.findById(stationId)
				.orElseThrow(
						() -> new NotRegisteredStationException(ErrorCode.CANNOT_FIND_PATH_WITH_NOT_REGISTERED_STATION.getMessage())
				);
	}
}
