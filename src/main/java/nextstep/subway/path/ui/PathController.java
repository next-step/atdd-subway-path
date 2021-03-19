package nextstep.subway.path.ui;

import nextstep.subway.line.exception.NotFoundException;
import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.exception.FirstStationEqualsFinalStationException;
import nextstep.subway.path.exception.NotConnectedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/path")
public class PathController {

    private final PathService pathService;

    public PathController(final PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> findPaths(@RequestParam long source, long target) {
        PathResponse pathResponse = pathService.findShortestPath(source, target);
        return ResponseEntity.ok(pathResponse);
    }

    @ExceptionHandler({
            FirstStationEqualsFinalStationException.class,
            NotFoundException.class,
            NotConnectedException.class,
            RuntimeException.class
    })
    public ResponseEntity subwayLineHandleException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
