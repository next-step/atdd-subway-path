package nextstep.subway.path.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.common.exception.InvalidStationPathException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.path.domain.Path;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
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

  public PathResponse findPath(Long sourceId, Long targetId) {
    if (sourceId.equals(targetId)) {
      throw new InvalidStationPathException("출발역과 도착역은 서로 달라야 합니다.");
    }
    Station sourceStation = stationService.findStation(sourceId);
    Station targetStation = stationService.findStation(targetId);
    List<Sections> sectionsList = filterSections(sourceStation, targetStation);
    Path path = PathFinder.of(sectionsList)
        .findPath(sourceStation, targetStation);
    return PathResponse.of(path);
  }

  private List<Sections> filterSections(Station sourceStation, Station targetStation) {
    return lineService.getAllLine().stream()
        .map(Line::getSections)
        .filter(sections -> sections.getSortedStations().contains(sourceStation) || sections
            .getSortedStations().contains(targetStation))
        .collect(Collectors.toList());
  }
}
