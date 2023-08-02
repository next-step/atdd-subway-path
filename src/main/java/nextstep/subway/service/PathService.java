package nextstep.subway.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.JgraphPathFinder;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinderStrategy;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.PathResponse;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PathService {
  private final LineRepository lineRepository;
  private final StationService stationService;
  public PathResponse findPath(Long source, Long target) {
    List<Line> lines = lineRepository.findAll();

    Station sourceStation = stationService.getStation(source);
    Station targetStation = stationService.getStation(target);

    PathFinderStrategy pathFinderStrategy = JgraphPathFinder.of(lines);
    Path path = pathFinderStrategy.findShortestPath(sourceStation, targetStation);

    return PathResponse.from(path);
  }
}
