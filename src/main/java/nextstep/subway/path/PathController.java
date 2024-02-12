package nextstep.subway.path;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/paths")
public class PathController {
    private final PathService pathService;

    @GetMapping
    public ResponseEntity<PathResponse> showShortestPath(
            @RequestParam("source") Long sourceId,
            @RequestParam("target") Long targetId
    ) {
        return ResponseEntity.ok().body(pathService.showShortestPath(sourceId, targetId));
    }
}
