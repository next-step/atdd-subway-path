package nextstep.subway.path.ui;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.PathResponse;
import nextstep.subway.path.application.PathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/paths")
public class PathController {
    private final PathService pathService;

    public PathController(final PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity getPath(@RequestParam long source, @RequestParam long target) {
        PathResponse pathResponse = pathService.getPath(source, target);
        return ResponseEntity.ok(pathResponse);
    }

}

