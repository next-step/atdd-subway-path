package nextstep.subway.path.ui;

import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PathController {

    private PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> searchShortestPath(@RequestParam("source") Long sourceStationId,
                                                           @RequestParam("target") Long targetStationIs) {
        PathResponse response = pathService.searchShortestPath(sourceStationId, targetStationIs);
        return ResponseEntity.ok().body(response);
    }

}
