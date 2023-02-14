package nextstep.subway.applicaion;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

@Service
@RequiredArgsConstructor
public class PathService {

	private final LineRepository lineRepository;

	private final StationRepository stationRepository;

	@Transactional(readOnly = true)
	public PathResponse getPath(Long source, Long target) {
		List<Line> lines = lineRepository.findAll();

		Station sourceStation = stationRepository.findById(source).orElseThrow(IllegalArgumentException::new);
		Station targetStation = stationRepository.findById(target).orElseThrow(IllegalArgumentException::new);

		PathFinder pathFinder = new PathFinder(lines, sourceStation, targetStation);
		SubwayPath subwayPath = pathFinder.findPath();
		return new PathResponse(subwayPath.getStations(), subwayPath.getMinimumDistance());
	}
}
