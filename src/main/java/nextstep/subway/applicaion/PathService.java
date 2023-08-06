package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.path.PathSearchRequest;
import nextstep.subway.applicaion.dto.path.PathSearchResponse;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
public class PathService {

  // TODO
  public PathSearchResponse searchAndGetPath(PathSearchRequest request, BindingResult bindingResult) {
    return null;
  }
}
