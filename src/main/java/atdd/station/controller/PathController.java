package atdd.station.controller;

import atdd.station.domain.Station;
import atdd.station.dto.path.PathFindResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(produces = "application/json")
public class PathController {
    @GetMapping("/paths")
    public ResponseEntity<PathFindResponseDto> findShortestPath(@RequestParam long startId, @RequestParam long endId) {
        List<Station> stations = new ArrayList<>(Arrays.asList(new Station(), new Station(), new Station(), new Station()));
        return ResponseEntity.ok().body(new PathFindResponseDto(startId, endId, stations));
    }
}
