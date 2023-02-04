package nextstep.subway.ui;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {

    private PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/paths/junghyun")
    public ResponseEntity<PathResponse> path(@RequestParam Long source, @RequestParam Long target) {
        PathResponse pathResponse = pathService.findShortestPath(source, target);

        return ResponseEntity.ok(pathResponse);
    }
}
