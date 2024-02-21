package nextstep.subway.presentation;

import nextstep.subway.application.PathService;
import nextstep.subway.dto.PathRequest;
import nextstep.subway.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {

    public final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> findShortestPath(@RequestParam("source") Long departureStationId,
                                                         @RequestParam("target") Long arrivalStationId) {
        return ResponseEntity.ok(pathService.findShortestPath(new PathRequest(departureStationId, arrivalStationId)));
    }
}
