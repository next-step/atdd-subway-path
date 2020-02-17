package atdd.station.controller;

import atdd.station.dto.path.PathFindResponseDto;
import atdd.station.service.PathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json")
public class PathController {
    private PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/paths")
    public ResponseEntity<PathFindResponseDto> findShortestPath(@RequestParam long startId, @RequestParam long endId) {
        PathFindResponseDto path = pathService.findPath(startId, endId);
        return ResponseEntity.ok().body(path);
    }
}
