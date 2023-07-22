package nextstep.subway.path;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.station.service.StationService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PathController {
    private final PathFindService pathFindService;
    private final StationService stationService;

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> getPaths(@RequestParam("source") Long sourceStationId, @RequestParam("target") Long targetStationId) {
        ShortestPath shortestPath = pathFindService.getPath(stationService.get(sourceStationId), stationService.get(targetStationId));

        return ResponseEntity.ok()
                             .body(new PathResponse(shortestPath));
    }
}

