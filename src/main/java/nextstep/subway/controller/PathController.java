package nextstep.subway.controller;

import lombok.RequiredArgsConstructor;
import nextstep.subway.dto.PathResponse;
import nextstep.subway.service.PathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PathController {

  private final PathService pathService;

  @PostMapping("/paths")
  public ResponseEntity<PathResponse> getPaths(@RequestParam Long source,
      @RequestParam Long target) {
    PathResponse pathResponse = pathService.findPath(source, target);

    return ResponseEntity.ok().body(pathResponse);
  }
}
