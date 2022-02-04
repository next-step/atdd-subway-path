package nextstep.subway.ui;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.query.PathQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathQueryService pathQueryService;

    public PathController(PathQueryService pathQueryService) {
        this.pathQueryService = pathQueryService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> shortestPath(@RequestParam Long source, @RequestParam Long target) {
        PathResponse response = pathQueryService.findPath(source, target);

        return ResponseEntity.ok(response);
    }

}
