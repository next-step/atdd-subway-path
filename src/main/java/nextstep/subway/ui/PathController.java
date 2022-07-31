package nextstep.subway.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
@RequiredArgsConstructor
public class PathController {

    private final PathFinder pathFinder;

    @GetMapping
    public ResponseEntity<PathResponse> findShortestPath(
            @RequestParam Long source,
            @RequestParam Long target
    ) {
        return ResponseEntity.ok(pathFinder.solve(source, target));
    }
}
