package nextstep.subway.map.ui;

import nextstep.subway.map.application.PathService;
import nextstep.subway.map.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {
    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> findShortPath(@RequestParam Long source, @RequestParam Long target) {
        return ResponseEntity.ok(pathService.findPath(source, target));
    }
}
