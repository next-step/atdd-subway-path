package nextstep.subway.path.web;

import lombok.AllArgsConstructor;
import nextstep.subway.path.application.PathService;
import nextstep.subway.path.application.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
@AllArgsConstructor
public class PathController {

    private final PathService pathService;

    @GetMapping
    public ResponseEntity<PathResponse> findShortestPath(@RequestParam Long source,
        @RequestParam Long target) {
        return ResponseEntity.ok().body(pathService.findShortestPath(source, target));
    }

}
