package nextstep.subway.applicaion;

import static java.util.stream.Collectors.toList;

import java.util.List;
import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.common.exception.CustomException;
import nextstep.subway.common.exception.message.PathErrorMessage;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PathService {

  private final StationRepository stationRepository;
  private final LineRepository lineRepository;

  public PathResponse getPath(Long sourceId, Long targetId) {
    Station sourceStation = stationRepository.findById(sourceId).orElseThrow();
    Station targetStation = stationRepository.findById(targetId).orElseThrow();

    PathResponse pathResponse = getPathResponse(sourceStation, targetStation);

    isStationsEmptyCheck(pathResponse);

    return pathResponse;
  }

  private PathResponse getPathResponse(Station sourceStation, Station targetStation) {
    PathResponse pathResponse = new PathResponse();
    List<Line> lines = lineRepository.findAll();
    for (Line line : lines) {

      if (lineContainStations(line, sourceStation,targetStation)) {
        getPathFinder(line, sourceStation, targetStation, pathResponse);
      }
    }

    return pathResponse;
  }

  private boolean lineContainStations(Line line, Station sourceStation, Station targetStation) {
    List<Station> stations = line.getSections().getAllStation();
    return stations.contains(sourceStation) && stations.contains(targetStation);
  }

  private void getPathFinder(Line line, Station sourceStation, Station targetStation, PathResponse pathResponse) {
    PathFinder pathFinder = new PathFinder(line.getSections().getSections());
    int pathDistance = pathFinder.getDistance(sourceStation, targetStation);

    if (pathResponse.getDistance() > pathDistance) {
      pathResponse.changeStations(getStationResponse(pathFinder.getPath(sourceStation, targetStation)));
      pathResponse.changeDistance(pathDistance);
    }
  }

  private List<StationResponse> getStationResponse(List<Station> stations) {
    return stations.stream()
        .map(StationResponse::createStationResponse)
        .collect(toList());
  }

  private void isStationsEmptyCheck(PathResponse pathResponse) {
    if (pathResponse.getStations().isEmpty()) {
      throw new CustomException(PathErrorMessage.PATH_STATION_EMPTY);
    }
  }
}
