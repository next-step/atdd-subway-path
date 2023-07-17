package nextstep.subway.ui.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.path.response.PathResponse;
import nextstep.subway.applicaion.path.PathService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/paths")
public class PathController {
    private final PathService pathService;

    @GetMapping
    public ResponseEntity<PathResponse> showShortestPath(@RequestParam("source") final Long sourceId,
                                                         @RequestParam("target") final Long targetId) {
        return ResponseEntity
                .ok()
                .body(pathService.findShortestPath(sourceId, targetId));
    }
}
