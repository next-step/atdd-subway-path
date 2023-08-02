package nextstep.subway.path;

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
    public ResponseEntity<PathResponse> getPathInfo(@RequestParam Long source, @RequestParam Long target) {
        PathResponse response = pathService.getPath(source, target);
        return ResponseEntity.ok()
                .body(response);
    }
}
