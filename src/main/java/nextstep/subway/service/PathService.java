package nextstep.subway.service;

import nextstep.subway.domain.Path;
import nextstep.subway.dto.PathResponse;
import nextstep.subway.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PathService {
	private StationService stationService;
	public PathService(StationService stationService) {
		this.stationService = stationService;
	}

	public PathResponse getPath(Long source, Long target) {
		List<Long> stations = Path.getPath(source, target);
		return new PathResponse(
				stations.stream()
						.map(id -> new StationResponse(id, stationService.findStationById(id).getName()))
						.collect(Collectors.toList())
				, (int) (Path.getDistance(source, target))
		);
	}
}
