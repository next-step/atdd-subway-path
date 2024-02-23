package nextstep.subway.path;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {
    private PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/path")
    public ResponseEntity<PathResponse> showPath(@RequestParam Long source, @RequestParam Long target) {
        PathResponse path = pathService.findPath(source, target);
        return ResponseEntity.ok().body(path);
    }
}
