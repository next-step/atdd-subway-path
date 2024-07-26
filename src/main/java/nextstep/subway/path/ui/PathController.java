package nextstep.subway.path.ui;

import nextstep.subway.path.payload.SearchPathRequest;
import nextstep.subway.path.payload.ShortestPathResponse;
import nextstep.subway.path.service.PathQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/path")
@RestController
public class PathController {

    private final PathQueryService pathQueryService;

    public PathController(final PathQueryService pathQueryService) {
        this.pathQueryService = pathQueryService;
    }

    @GetMapping
    public ResponseEntity<ShortestPathResponse> findShortestPath(final SearchPathRequest request) {
        return ResponseEntity.ok(pathQueryService.findShortestPath(request));
    }

}
