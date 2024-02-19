package nextstep.subway.line.controller;

import nextstep.subway.path.PathResponse;
import nextstep.subway.path.PathService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/paths")
    public PathResponse showPath(
            @RequestParam Long source,
            @RequestParam Long target
    ) {
        return pathService.getPath(source, target);
    }
}
