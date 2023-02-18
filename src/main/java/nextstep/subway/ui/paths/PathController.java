package nextstep.subway.ui.paths;

import nextstep.subway.applicaion.dto.path.PathResponse;
import nextstep.subway.applicaion.path.PathFinder;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathController(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @GetMapping
    public ResponseEntity<PathResponse> searchPath(@RequestParam Long source, @RequestParam Long target) {
        Station sourceStation = stationRepository.findById(source).orElseThrow(IllegalArgumentException::new);
        Station targetStation = stationRepository.findById(target).orElseThrow(IllegalArgumentException::new);

        return ResponseEntity.ok().body(PathFinder.findPath(lineRepository.findAll(), sourceStation, targetStation));
    }
}
