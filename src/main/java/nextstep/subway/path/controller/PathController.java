package nextstep.subway.path.controller;

import nextstep.subway.path.controller.dto.PathResponse;
import nextstep.subway.path.service.PathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> searchPath(Long source, Long target) {
        return ResponseEntity.ok().body(pathService.searchPath(source, target));
    }
}
