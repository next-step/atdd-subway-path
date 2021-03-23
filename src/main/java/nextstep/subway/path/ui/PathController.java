package nextstep.subway.path.ui;

import nextstep.subway.path.applicaiton.PathService;
import nextstep.subway.path.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/paths")
public class PathController {

    private PathService pathService;
    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> getShortestPath(@RequestParam Long source, @RequestParam Long target){
        PathResponse response = pathService.getShortestPath(source, target);
        return ResponseEntity.ok().body(response);
    }
}
