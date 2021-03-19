package nextstep.subway.path.ui;

import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.ShortestPathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/paths")
public class PathController {

    PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity shortestPath(@RequestParam Long source, @RequestParam Long target){
        ShortestPathResponse shortestPathResponse = pathService.getShortestPath(source, target);
        return ResponseEntity.ok().body(shortestPathResponse);
    }
}
