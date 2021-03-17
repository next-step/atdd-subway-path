package nextstep.subway.path.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.common.exception.InvalidStationPathException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.domain.StationPath;
import nextstep.subway.path.dto.PathStationResponse;
import nextstep.subway.path.dto.StationPathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

@Service
public class PathService {

  private LineService lineService;
  private StationService stationService;

  public PathService(LineService lineService, StationService stationService) {
    this.lineService = lineService;
    this.stationService = stationService;
  }

  public StationPathResponse findPath(Long sourceId, Long targetId) {
    if (sourceId.equals(targetId)) {
      throw new InvalidStationPathException("출발역과 도착역은 서로 달라야 합니다.");
    }
    List<Sections> sections = lineService.getLineByStationId(sourceId, targetId)
        .stream()
        .map(Line::getSections)
        .collect(Collectors.toList());
    Station sourceStation = stationService.findStation(sourceId);
    Station targetStation = stationService.findStation(targetId);

    StationPath stationPath = PathFinder.findPath(sections, sourceStation, targetStation);
    List<PathStationResponse> pathStationResponses = stationPath.getStations()
        .stream()
        .map(PathStationResponse::of)
        .collect(Collectors.toList());

    return StationPathResponse.of(pathStationResponses, stationPath.getDistance());
  }
}
