package nextstep.subway.path.ui;

import nextstep.subway.path.application.PathFinderService;
import nextstep.subway.path.domain.exception.CannotFindPathException;
import nextstep.subway.station.domain.Stations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PathController {

    private final PathFinderService pathFinderService;

    public PathController(PathFinderService pathFinderService) {
        this.pathFinderService = pathFinderService;
    }


    @GetMapping("/paths")
    public ResponseEntity findShortestPath(@RequestParam Long source, @RequestParam Long target){
        Stations stations = pathFinderService.findShortestPath(source, target);
        return ResponseEntity.ok(stations);
    }

    @ExceptionHandler(CannotFindPathException.class)
    public ResponseEntity cannotFindPathExceptionHandler(CannotFindPathException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
