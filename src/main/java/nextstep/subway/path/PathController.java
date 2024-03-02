package nextstep.subway.path;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/paths")
@RestController
public class PathController {

    private final PathFinderService pathFinderService;

    public PathController(PathFinderService pathFinderService) {
        this.pathFinderService = pathFinderService;
    }

    @GetMapping
    public ResponseEntity<PathFoundResponse> findPath(
            @RequestParam Long source,
            @RequestParam Long target
    ) {
        PathFoundResponse response = pathFinderService.findPath(source, target);
        return ResponseEntity.ok().body(response);
    }
}
