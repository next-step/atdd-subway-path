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

    PathResponse pathResponse = new PathResponse();
    List<Line> lines = lineRepository.findAll();
    for (Line line : lines) {
      List<Station> stations = line.getSections().getAllStation();
      if (stations.contains(sourceStation) && stations.contains(targetStation)) {
        PathFinder pathFinder = new PathFinder(line.getSections().getSections());
        List<Station> pathStations = pathFinder.getPath(sourceStation, targetStation);
        int pathDistance = pathFinder.getDistance(sourceStation, targetStation);

        if (pathResponse.getDistance() > pathDistance) {
          List<StationResponse> stationResponses = pathStations.stream()
              .map(StationResponse::createStationResponse)
              .collect(toList());

          pathResponse.changeStations(stationResponses);
          pathResponse.changeDistance(pathDistance);
        }
      }
    }

    if (pathResponse.getStations().isEmpty()) {
      throw new CustomException(PathErrorMessage.PATH_STATION_EMPTY);
    }

    return pathResponse;
  }
}
