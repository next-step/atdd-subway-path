package nextstep.subway.path.ui;

import nextstep.subway.path.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/paths")
public class PathController {

    @GetMapping
    public ResponseEntity<PathResponse>
    getShortestPath(
            @RequestParam("source") Long sourceStationId,
            @RequestParam("target") Long destinationStationId) {
        final PathResponse pr = new PathResponse(new ArrayList<>(), 40, 40);
        return ResponseEntity.ok(pr);
    }
}
