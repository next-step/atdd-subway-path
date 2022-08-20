package nextstep.subway.ui;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paths")
public class PathController {
    private PathService pathSerivce;

    public PathController(PathService pathService) {
        this.pathSerivce = pathService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> showPaths(@RequestParam Long source, @RequestParam Long target) {
        PathResponse response = pathSerivce.showPaths(source, target);
        return ResponseEntity.ok().body(response);
    }

}