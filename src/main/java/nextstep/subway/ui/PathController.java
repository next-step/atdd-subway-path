package nextstep.subway.ui;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.path.PathSearchRequest;
import nextstep.subway.applicaion.dto.path.PathSearchResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
@RequiredArgsConstructor
public class PathController {

  private final PathService pathService;

  @GetMapping
  public PathSearchResponse getPathBetween(@Valid PathSearchRequest request, BindingResult bindingResult) {
    return pathService.searchAndGetPath(request, bindingResult);
  }
}
