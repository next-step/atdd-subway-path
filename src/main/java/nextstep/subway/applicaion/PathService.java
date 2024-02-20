package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.FindPathResponse;
import nextstep.subway.ui.BusinessException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PathService {

  private final SectionService sectionService;
  private final StationService stationService;
  private final PathFinder pathFinder;

  public FindPathResponse findPath(Long source, Long target) {
    verifySourceIsSameToTarget(source, target);

    final var sourceStation = stationService.getStation(source)
        .orElseThrow(() -> new BusinessException("출발역 정보를 찾을 수 없습니다."));
    final var targetStation = stationService.getStation(target)
        .orElseThrow(() -> new BusinessException("출발역 정보를 찾을 수 없습니다."));

    final var sections = sectionService.findAll();
    final var path = pathFinder.find(sections, sourceStation, targetStation)
        .orElseThrow(() -> new BusinessException("경로를 찾을 수 없습니다."));

    return new FindPathResponse(path.getVertices(), path.getDistance());
  }

  private void verifySourceIsSameToTarget(Long source, Long target) {
    if (source.equals(target)) {
      throw new BusinessException("출발역과 도착역이 같습니다.");
    }
  }
}
