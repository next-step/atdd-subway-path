package nextstep.subway.ui;

import nextstep.subway.applicaion.PathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {

    private final PathService pathService;

    public PathController(final PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/paths")
    public ResponseEntity<?> getShortestPath(
            @RequestParam("source") final Long sourceId,
            @RequestParam("target") final Long targetId) {
        return ResponseEntity.ok().body(pathService.findShortestPath(sourceId, targetId));
    }
}
