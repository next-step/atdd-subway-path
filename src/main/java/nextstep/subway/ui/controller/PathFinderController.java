package nextstep.subway.ui.controller;

import nextstep.subway.applicaion.service.PathFinderService;
import nextstep.subway.applicaion.dto.path.PathFinderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathFinderController {

    private final PathFinderService pathFinderService;

    public PathFinderController(PathFinderService pathFinderService) {
        this.pathFinderService = pathFinderService;
    }

    @GetMapping
    public ResponseEntity<PathFinderResponse> findPath(@RequestParam(name = "source") Long sourceId,
                                                       @RequestParam(name = "target") Long targetId) {
        PathFinderResponse pathFinderResponse = pathFinderService.findPath(sourceId, targetId);
        return ResponseEntity.ok().body(pathFinderResponse);
    }
}