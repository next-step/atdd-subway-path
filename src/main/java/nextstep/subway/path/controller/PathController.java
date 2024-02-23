package nextstep.subway.path.controller;

import nextstep.subway.path.controller.dto.PathResponse;
import nextstep.subway.path.service.PathService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/paths")
@RestController
public class PathController {

    private final PathService service;

    public PathController(PathService service) {
        this.service = service;
    }

    @GetMapping
    public PathResponse getPaths(
        @RequestParam Long source,
        @RequestParam Long target
    ) {
        return service.getPaths(source, target);
    }
}
