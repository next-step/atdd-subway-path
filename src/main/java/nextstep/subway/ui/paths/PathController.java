package nextstep.subway.ui.paths;

import nextstep.subway.applicaion.path.PathFinder;
import nextstep.subway.applicaion.dto.path.PathResponse;
import nextstep.subway.domain.line.LineRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {
    private LineRepository lineRepository;

    public PathController(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @GetMapping
    public ResponseEntity<PathResponse> searchPath(@RequestParam Long source, @RequestParam Long target) {
        return ResponseEntity.ok().body(PathFinder.findPath(lineRepository.findAll(), source, target));
    }
}
