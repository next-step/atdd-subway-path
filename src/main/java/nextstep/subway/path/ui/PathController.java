package nextstep.subway.path.ui;

import nextstep.subway.path.application.PathFacade;
import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.dto.PathType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathFacade pathFacade;

    public PathController(PathFacade pathFacade) {
        this.pathFacade = pathFacade;
    }

    @GetMapping
    public ResponseEntity<PathResponse> findShortestPath(@RequestParam("source") Long startStationId, @RequestParam("target") Long endStationId,
                                                         @RequestParam("type") PathType type) {
        return ResponseEntity.ok(pathFacade.findPath(startStationId, endStationId, type));
    }


}
