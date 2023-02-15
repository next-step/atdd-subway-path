package nextstep.subway.ui;

import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {
    private PathFinder pathFinder;

    public PathController(PathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }

    @GetMapping
    public PathResponse searchPath(@RequestParam Long source, @RequestParam Long target) {
        return pathFinder.searchPath(source, target);
    }
}
