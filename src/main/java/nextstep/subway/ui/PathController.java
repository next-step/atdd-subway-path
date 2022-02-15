package nextstep.subway.ui;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class PathController {
  private final PathService pathService;

  public PathController(PathService pathService) {
    this.pathService = pathService;
  }

  @GetMapping("/paths")
  public ResponseEntity<PathResponse> searchShortestPath(@RequestParam Long source, @RequestParam Long target) {
    PathResponse response = pathService.findShortestPath(source, target);
    LoggerFactory.getLogger(this.getClass()).info("[searchShortestPath] response = {}", response);
    return ResponseEntity.ok().body(response);
  }
}
