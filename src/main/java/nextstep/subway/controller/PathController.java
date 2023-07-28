package nextstep.subway.controller;

import nextstep.subway.facade.PathFacade;
import nextstep.subway.service.response.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {

    private final PathFacade pathFacade;

    public PathController(PathFacade pathFacade) {
        this.pathFacade = pathFacade;
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> getPath(
        @RequestParam long source, @RequestParam long target) {

        return ResponseEntity.ok(pathFacade.getPath(source, target));
    }
}

