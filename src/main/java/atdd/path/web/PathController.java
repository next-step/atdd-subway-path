package atdd.path.web;

import atdd.path.domain.Station;
import atdd.path.dto.PathResponseView;
import atdd.path.serivce.PathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static atdd.path.PathConstant.BASE_URI_PATH;

@RestController
@RequestMapping(value = BASE_URI_PATH)
public class PathController {
    private PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity findPath(@RequestParam("startId") Long startId,
                                   @RequestParam("endId") Long endId) {
        List<Station> stationsInPath = pathService.findPath(startId, endId);
        PathResponseView responseView = PathResponseView.builder()
                .startStationId(startId)
                .endStationId(endId)
                .stations(stationsInPath)
                .build();

        return ResponseEntity
                .ok()
                .body(responseView);
    }
}