package nextstep.subway.ui;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.ui.response.PathResponse;
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
    public ResponseEntity<PathResponse> findShortestPath(
            @RequestParam Long source,
            @RequestParam Long target
    ) {
        PathResponse pathResponse = pathService.findShortestPath(source, target);
        return ResponseEntity.ok().body(pathResponse);
    }
}
