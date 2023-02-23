package nextstep.subway.path;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.section.SectionRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationService;

@Service
@RequiredArgsConstructor
public class PathService {

	private final SectionRepository sectionRepository;
	private final StationService stationService;

	public PathResponse pathSearch(Long source, Long target) {
		Station sourceStation = stationService.findStationById(source);
		Station targetStation = stationService.findStationById(target);
		PathFinder pathFinder = new PathFinder(stationService.findAllStation(), sectionRepository.findAll());
		return pathFinder.pathSearch(sourceStation, targetStation);
	}
}
