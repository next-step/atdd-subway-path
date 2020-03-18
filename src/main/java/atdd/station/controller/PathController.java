package atdd.station.controller;

import atdd.station.model.dto.PathResponseView;
import atdd.station.model.entity.Station;
import atdd.station.service.PathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/paths")
public class PathController {
    @Autowired
    private PathService pathService;

    @GetMapping
    public ResponseEntity<PathResponseView> findPath(@RequestParam long startId, @RequestParam long endId) {
        List<Station> pathStations = pathService.findPath(startId, endId);

        return ResponseEntity.ok(PathResponseView.of(startId, endId, pathStations));
    }
}
