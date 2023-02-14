package nextstep.subway.ui;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.SubwayPath;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;
    private final StationService stationService;

    public PathController(PathService pathService, StationService stationService) {
        this.pathService = pathService;
        this.stationService = stationService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> findPaths(@RequestParam Long source, @RequestParam Long target) {
        SubwayPath subwayPath = pathService.findPath(stationService.findById(source), stationService.findById(target));
        return ResponseEntity.ok().body(PathResponse.of(subwayPath));
    }
}
