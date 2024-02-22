package nextstep.subway.controller;

import nextstep.subway.dto.path.PathResponse;
import nextstep.subway.service.PathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paths")
public class PathController {
    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> getPaths(
        @RequestParam Long source,
        @RequestParam Long target
    ) {
        PathResponse response = pathService.getPaths(source, target);

        return ResponseEntity
            .ok()
            .body(response);
    }
}
