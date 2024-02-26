package nextstep.subway.controller;

import nextstep.subway.controller.dto.PathResponse;
import nextstep.subway.service.PathService;
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

    @GetMapping(value = "/paths")
    public ResponseEntity<PathResponse> findPath(
            @RequestParam("source") Long source, @RequestParam("target") Long target
    ) {
        return ResponseEntity.ok().body(pathService.findPath(source, target));
    }
}
