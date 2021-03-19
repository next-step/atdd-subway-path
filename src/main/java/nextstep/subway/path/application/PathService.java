package nextstep.subway.path.application;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.stereotype.Service;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

@Service
public class PathService {

	private final LineService lineService;
	private final StationService stationService;

	public PathService(LineService lineService, StationService stationService) {
		this.lineService = lineService;
		this.stationService = stationService;
	}

	public PathResponse searchPath(Long sourceId, Long targetId) {
		final Station startStation = stationService.findStationById(sourceId);
		final Station endStation = stationService.findStationById(targetId);
		final List<Section> sections = lineService.findAllSections();

		final PathFinder pathFinder = PathFinder.of(sections);

		return PathResponse.of(
			pathFinder.findShortestPath(startStation, endStation)
				.stream()
				.map(StationResponse::of)
				.collect(toList()),
			pathFinder.findShortestPathDistance(startStation, endStation)
		);
	}

}
