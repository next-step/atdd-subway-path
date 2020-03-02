package atdd.path.controller;

import atdd.path.domain.Graph;
import atdd.path.domain.Station;
import atdd.path.domain.dto.RouteResponseDto;
import atdd.path.domain.dto.StationDto;
import atdd.path.repository.LineRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("routes")
public class RouteController {

    private final LineRepository lineRepository;

    public RouteController(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @GetMapping("distance")
    public ResponseEntity findShortestDistancePath(@RequestParam("startId") Long startId, @RequestParam("endId") Long endId) {
        Graph graph = new Graph(lineRepository.findAll());
        List<Station> paths = graph.getShortestDistancePath(startId, endId);

        return ResponseEntity.ok()
                .body(RouteResponseDto.builder()
                        .startStationId(startId)
                        .endStationId(endId)
                        .stations(StationDto.listOf(paths))
                        .estimatedTime(graph.getEstimatedTime(startId, endId))
                        .build());
    }

    @GetMapping("time")
    public ResponseEntity findShortestTimePath(@RequestParam("startId") Long startId, @RequestParam("endId") Long endId) {
        Graph graph = new Graph(lineRepository.findAll());
        List<Station> paths = graph.getShortestTimePath(startId, endId);

        return ResponseEntity.ok()
                .body(RouteResponseDto.builder()
                        .startStationId(startId)
                        .endStationId(endId)
                        .stations(StationDto.listOf(paths))
                        .estimatedTime(graph.getEstimatedTime(startId, endId))
                        .build());
    }
}
