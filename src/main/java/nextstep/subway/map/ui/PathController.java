package nextstep.subway.map.ui;

import nextstep.subway.map.application.PathService;
import nextstep.subway.map.dto.PathRequest;
import nextstep.subway.map.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {
    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> findShortestPath(PathRequest request) {
        PathResponse pathResponse = pathService.findShortestPath(request);

        return ResponseEntity.ok(pathResponse);
    }
}
