package nextstep.subway.ui;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.ShortestPathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {
    private final LineService lineService;

    public PathController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping
    public ResponseEntity<ShortestPathResponse> findShortestPath(@RequestParam Long source, @RequestParam Long target) {
        return ResponseEntity.ok(lineService.findShortestPath(source, target));
    }
}
