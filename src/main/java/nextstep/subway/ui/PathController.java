package nextstep.subway.ui;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.http.MediaType;
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

    @GetMapping(
        value = "/paths",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PathResponse> getPath(@RequestParam final long sourceId, @RequestParam final long targetId) {
        return ResponseEntity.ok(pathService.findPath(sourceId, targetId));
    }
}
