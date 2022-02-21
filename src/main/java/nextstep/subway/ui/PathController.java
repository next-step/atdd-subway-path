package nextstep.subway.ui;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.ShortestPathResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ShortestPathResponse getShortestPath(@RequestParam("source") Long sourceId, @RequestParam("target") Long targetId) {
        return pathService.getShortestPath(sourceId, targetId);
    }
}
