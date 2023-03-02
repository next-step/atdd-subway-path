package nextstep.subway.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.PathFinderService;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paths")
@RequiredArgsConstructor
public class PathFinderController {

    private final PathFinderService pathFinderService;

    @GetMapping
    public ResponseEntity<PathResponse> findShortestPath(
            @RequestParam("source") final long sourceStationId,
            @RequestParam("target") final long targetStationId) {
        final PathResponse pathResponse = pathFinderService.findShortestPath(sourceStationId, targetStationId);
        return ResponseEntity.ok(pathResponse);
    }
}
