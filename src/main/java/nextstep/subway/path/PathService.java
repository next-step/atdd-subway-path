package nextstep.subway.path;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.section.SectionRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;

@Service
@RequiredArgsConstructor
public class PathService {

	private final SectionRepository sectionRepository;
	private final StationRepository stationRepository;

	public PathResponse pathSearch(Long source, Long target) {
		PathFinder pathFinder = new PathFinder(stationRepository.findAll(), sectionRepository.findAll());
		Station sourceStation = stationRepository.findById(source).orElseThrow(() -> new IllegalArgumentException("Station doesn't exist"));
		Station targetStation = stationRepository.findById(target).orElseThrow(() -> new IllegalArgumentException("Station doesn't exist"));
		return pathFinder.pathSearch(sourceStation, targetStation);
	}
}
