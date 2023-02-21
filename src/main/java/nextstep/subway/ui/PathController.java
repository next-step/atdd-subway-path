package nextstep.subway.ui;

import nextstep.subway.applicaion.PathFinderService;
import nextstep.subway.domain.dto.PathResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathFinderService pathFinderService;

    public PathController(PathFinderService pathFinderService) {
        this.pathFinderService = pathFinderService;
    }

    @GetMapping
    public PathResponse getPath(@RequestParam Long source,
                                @RequestParam Long target) {
        return pathFinderService.findPath(source, target);
    }
}
