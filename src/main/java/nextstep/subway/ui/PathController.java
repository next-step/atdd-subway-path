package nextstep.subway.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PathController {

  private final PathService pathService;

  @GetMapping("/paths")
  public PathResponse getPath(@RequestParam Long source, @RequestParam Long target) {
    return pathService.getPath(source, target);
  }
}
