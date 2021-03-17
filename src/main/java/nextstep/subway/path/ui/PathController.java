package nextstep.subway.path.ui;

import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.exception.DoseNotConnectedException;
import nextstep.subway.path.exception.EqualsStationsException;
import nextstep.subway.station.exception.DoseNotExistedStationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/path")
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity getPath(
        @RequestParam long source,
        @RequestParam long target
    ) {
        PathResponse pathResponse = pathService.getPath(source, target);
        return ResponseEntity.ok(pathResponse);
    }

    @ExceptionHandler({
        DoseNotConnectedException.class,
        DoseNotExistedStationException.class,
        EqualsStationsException.class
    })
    public ResponseEntity handleException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
