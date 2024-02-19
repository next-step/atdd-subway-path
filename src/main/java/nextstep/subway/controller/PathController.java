package nextstep.subway.controller;

import nextstep.subway.domain.response.PathResponse;
import nextstep.subway.service.PathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("")
    public ResponseEntity<PathResponse> findShortestPath(@RequestParam("source") Long sourceId,
                                                         @RequestParam("target") Long targetId) {
        return ResponseEntity.ok().body(pathService.findShortestPath(sourceId, targetId));
    }
}
