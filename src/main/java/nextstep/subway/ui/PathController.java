package nextstep.subway.ui;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/paths")
public class PathController {

    @GetMapping
    public ResponseEntity<PathResponse> showPath() {
        return ResponseEntity.ok().body(new PathResponse(fakeStations(), 5));
    }

    private List<StationResponse> fakeStations() {
        return List.of(
                new StationResponse(1L, "교대역"),
                new StationResponse(4L, "남부터미널역"),
                new StationResponse(3L, "양재역")
        );
    }
}
