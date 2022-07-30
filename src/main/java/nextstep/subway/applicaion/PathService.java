package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PathService {

	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public PathResponse findPath(long source, long target) {
		Station departure = stationRepository.findById(source).orElseThrow(NoSuchElementException::new);
		Station destination = stationRepository.findById(target).orElseThrow(NoSuchElementException::new);
		PathFinder pathFinder = new PathFinder(lineRepository.findAll());
		return pathFinder.findPath(departure, destination);
	}
}
