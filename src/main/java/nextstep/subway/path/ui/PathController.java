package nextstep.subway.path.ui;

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

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/shortest")
    public ResponseEntity<PathResponse> findShortestPath(@RequestParam("startStationId") Long startStationId, @RequestParam("endStationId") Long endStationId) {
        return ResponseEntity.ok(pathService.findShortestPath(startStationId, endStationId));
    }

}
