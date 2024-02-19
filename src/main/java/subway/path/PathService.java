package subway.path;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import subway.dto.path.PathResponse;
import subway.line.Line;
import subway.line.LineRepository;
import subway.station.Station;

@Transactional(readOnly = true)
@Service
public class PathService {
	private final LineRepository lineRepository;

	public PathService(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	public PathResponse findShortestPath(Station sourceStation, Station targetStation) {
		List<Line> lines = lineRepository.findAll();
		FindPathAdapter findPathAdapter = FindPathAdapter.of(lines);

		return new PathResponse(
			findPathAdapter.getPath(sourceStation, targetStation),
			findPathAdapter.getPathWeight(sourceStation, targetStation)
		);
	}
}
