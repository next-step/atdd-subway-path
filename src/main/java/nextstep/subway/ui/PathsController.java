package nextstep.subway.ui;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathsResponse;
import nextstep.subway.domain.Paths;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathsController {

    private final PathService pathService;
    private final StationService stationService;

    public PathsController(PathService pathService, StationService stationService) {
        this.pathService = pathService;
        this.stationService = stationService;
    }

    @GetMapping
    public ResponseEntity<PathsResponse> findPaths(@RequestParam Long source, @RequestParam Long target) {
        Paths paths = pathService.findPath(stationService.findById(source), stationService.findById(target));
        return ResponseEntity.ok().body(PathsResponse.of(paths));
    }
}
