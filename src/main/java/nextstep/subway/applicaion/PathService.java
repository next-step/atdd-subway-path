package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {

  private final LineService lineService;
  private final StationService stationService;

  public PathService(LineService lineService, StationService stationService) {
    this.lineService = lineService;
    this.stationService = stationService;
  }

  public PathResponse findShortestPath(Long source, Long target) {
    List<Line> lines = lineService.getLines();
    Station sourceStation = stationService.findById(source);
    Station targetStation = stationService.findById(target);
    PathFinder pathFinder = new PathFinder(lines);
    return pathFinder.findShortestPath(sourceStation, targetStation);
  }

}
