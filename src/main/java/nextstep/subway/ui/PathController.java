package nextstep.subway.ui;

import nextstep.subway.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PathController {

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public PathController(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> path(@RequestParam Long source, @RequestParam Long target) {
        Station sourceStation = stationRepository.findById(source).orElseThrow(IllegalArgumentException::new);
        Station targetStation = stationRepository.findById(target).orElseThrow(IllegalArgumentException::new);

        List<Line> allLines = lineRepository.findAll();
        PathFinder pathFinder = PathFinder.create(allLines);
        PathResponse pathResponse = pathFinder.findShortestPath(sourceStation, targetStation);

        return ResponseEntity.ok(pathResponse);
    }
}
