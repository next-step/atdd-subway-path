package nextstep.subway.path.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.common.exception.InvalidStationPathException;
import nextstep.subway.common.exception.NoResourceException;
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
    Station sourceStation = stationService.findStation(sourceId);
    Station targetStation = stationService.findStation(targetId);
    List<Sections> sectionsList =  filterSections(sourceStation,targetStation);
    if(sectionsList.size() == 0) {
      throw new InvalidStationPathException("시작역이나 도착역이 노선에 등록되어 있지 않습니다.");
    }
    StationPath stationPath = PathFinder.findPath(sectionsList, sourceStation, targetStation);
    List<PathStationResponse> pathStationResponses = stationPath.getStations()
        .stream()
        .map(PathStationResponse::of)
        .collect(Collectors.toList());
    return StationPathResponse.of(pathStationResponses, stationPath.getDistance());
  }
  private List<Sections> filterSections(Station sourceStation, Station targetStation){
    return lineService.getAllLine().stream()
        .map(Line::getSections)
        .filter(sections -> sections.getSortedStations().contains(sourceStation) || sections.getSortedStations().contains(targetStation))
        .collect(Collectors.toList());
  }
}
