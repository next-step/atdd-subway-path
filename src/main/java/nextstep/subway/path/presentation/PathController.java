package nextstep.subway.path.presentation;

import nextstep.subway.path.presentation.response.FindPathResponse;
import nextstep.subway.path.service.PathService;
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

    @GetMapping("/paths")
    public ResponseEntity<FindPathResponse> findShortestPath(@RequestParam Long startStationId, @RequestParam Long endStationId) {
        FindPathResponse path = pathService.findShortestPath(startStationId, endStationId);
        return ResponseEntity.ok().body(path);
    }

}
