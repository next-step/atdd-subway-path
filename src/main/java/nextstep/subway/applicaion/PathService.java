package nextstep.subway.applicaion;

import java.util.List;
import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.path.PathSearchRequest;
import nextstep.subway.applicaion.dto.path.PathSearchResponse;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.line.Line;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.exception.ValidationExceptionCode;
import nextstep.subway.utils.BindingResultUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
public class PathService {

  private final LineService lineService;
  private final StationService stationService;

  @Transactional(readOnly = true)
  public PathSearchResponse searchAndGetPath(PathSearchRequest request, BindingResult bindingResult) {

    if(bindingResult.hasErrors()) {
      throw new SubwayException(
          ValidationExceptionCode.CONTAINS_INVALID_FIELD,
          String.join(",", BindingResultUtils.getAllErrorMessagesFrom(bindingResult))
      );
    }

    Station startStation = stationService.findById(request.getSource());
    Station targetStation = stationService.findById(request.getTarget());
    List<Line> allLines = lineService.getAllLines();

    PathFinder pathFinder = new PathFinder(allLines, startStation, targetStation);

    return pathFinder.findPath();
  }
}
