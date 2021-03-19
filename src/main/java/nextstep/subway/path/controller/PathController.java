package nextstep.subway.path.controller;

import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {

  private PathService pathService;

  public PathController(PathService pathService) {
    this.pathService = pathService;
  }

  @GetMapping
  public ResponseEntity getPath(@RequestParam long source, @RequestParam long target) {
    PathResponse response = pathService.findPath(source, target);
    return ResponseEntity.ok()
        .body(response);
  }
}
