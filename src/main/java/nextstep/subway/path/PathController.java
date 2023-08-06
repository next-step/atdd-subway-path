package nextstep.subway.path;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {
    private final PathService pathService;

    public PathController(final PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping(params = {"source", "target"})
    public PathResponse findPath(final Long source, final Long target) {
        return pathService.findPath(source, target);
    }

}
