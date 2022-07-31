package nextstep.subway.ui;

import java.util.List;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {

    @GetMapping
    public ResponseEntity<PathResponse> findShortestPath(
            @RequestParam Integer source,
            @RequestParam Integer target
    ) {
        return ResponseEntity.ok(new PathResponse(
                List.of(
                        new StationResponse(1L, "교대역"),
                        new StationResponse(4L, "남부터미널역"),
                        new StationResponse(3L, "양재역")
                ),
                5
        ));
    }
}
