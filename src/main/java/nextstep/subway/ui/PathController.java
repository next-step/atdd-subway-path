package nextstep.subway.ui;

import nextstep.subway.application.PathService;
import nextstep.subway.application.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paths")
public class PathController {

    private PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> getPath(@RequestParam Long source, @RequestParam Long target) {
        PathResponse pathResponse = pathService.getPath(source, target);
        return ResponseEntity.ok().body(pathResponse);
    }
}
