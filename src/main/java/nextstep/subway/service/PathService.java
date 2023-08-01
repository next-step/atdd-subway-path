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
  private final StationRepository stationRepository;
  public PathResponse findPath(Long source, Long target) {
    List<Line> lines = lineRepository.findAll();

    Station sourceStation = stationRepository.findById(source).orElseThrow(() -> new RuntimeException("존재하지 않는 출발역입니다."));
    Station targetStation = stationRepository.findById(target).orElseThrow(() -> new RuntimeException("존재하지 않는 도착역입니다."));;

    PathFinderStrategy pathFinderStrategy = JgraphPathFinder.of(lines);
    Path path = pathFinderStrategy.findShortestPath(sourceStation, targetStation);

    return PathResponse.from(path);
  }
}
