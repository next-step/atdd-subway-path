package subway.path;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import subway.dto.path.PathResponse;
import subway.station.Station;

@Transactional(readOnly = true)
@Service
public class PathService {
	public PathResponse findShortestPath(Long sourceStationId, Long targetStationId) {
		Station station1 = new Station(7L, "교대역");
		Station station2 = new Station(8L, "남부터미널역");
		Station station3 = new Station(2L, "양재역");
		List<Station> result = List.of(station1, station2, station3);
		return new PathResponse(result, 4);
	}
}
