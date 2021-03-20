package nextstep.subway.path.ui;

import nextstep.subway.line.application.LineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final LineService lineService;

    public PathController(final LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping
    public ResponseEntity getShortestPath(@RequestParam Long source, @RequestParam Long target) {
        return null;
    }
}
