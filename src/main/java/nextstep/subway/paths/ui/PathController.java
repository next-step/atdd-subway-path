package nextstep.subway.paths.ui;

import nextstep.subway.line.exception.InvalidStationException;
import nextstep.subway.paths.application.PathService;
import nextstep.subway.paths.dto.PathResponse;
import nextstep.subway.paths.exception.NotFoundStationPath;
import nextstep.subway.station.exception.NoSuchStationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;

    public PathController(final PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("")
    public ResponseEntity searchPath(@RequestParam Long source, @RequestParam Long target) {
        final PathResponse response = pathService.searchShortestPath(source, target);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(NotFoundStationPath.class)
    public ResponseEntity handleNotFoundStationPath(NotFoundStationPath e) {
        HashMap<String, String> responseBody = new HashMap();
        responseBody.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(responseBody);
    }

    @ExceptionHandler(NoSuchStationException.class)
    public ResponseEntity handleNoSuchStationException(NoSuchStationException e) {
        HashMap<String, String> responseBody = new HashMap();
        responseBody.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(responseBody);
    }
}
