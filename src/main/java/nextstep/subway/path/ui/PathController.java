package nextstep.subway.path.ui;

import org.springframework.http.ResponseEntity;

import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.ShortestPathResponse;

public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    public ResponseEntity<ShortestPathResponse> findShortestPath(Long startId, Long endId) {
        return ResponseEntity.ok().body(pathService.findShortestPath(startId, endId));
    }
}
