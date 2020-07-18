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

    @GetMapping
    public ResponseEntity<PathResponse>
    getShortestPath(
            @RequestParam("source") Long sourceStationId,
            @RequestParam("target") Long destinationStationId,
            @RequestParam("type") FindType type) {
        final PathResponse response = pathService.findShortestPath(sourceStationId, destinationStationId, type);
        return ResponseEntity.ok(response);
    }
}
