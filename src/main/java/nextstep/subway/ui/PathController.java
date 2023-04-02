package nextstep.subway.ui;

import nextstep.subway.applicaion.PathFindService;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paths")
public class PathController {

    private PathFindService pathFindService;

    public PathController(PathFindService pathFindService) {
        this.pathFindService = pathFindService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> getShortPath(@RequestParam Long source, @RequestParam Long target) {
        PathResponse pathResponse = pathFindService.findShortPath(source, target);
        return ResponseEntity.ok().body(pathResponse);
    }



}
