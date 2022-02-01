package nextstep.subway.ui;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.PathStationResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping( "/paths")
    public ResponseEntity<PathResponse> showShortestPath(@RequestParam("source") Long source,
                                                         @RequestParam("target") Long target) {
        PathResponse body = pathService.showShortestPath(source, target);

        PathStationResponse 교대역 = new PathStationResponse(1L, "교대역", LocalDateTime.now());
        PathStationResponse 남부터미널역 = new PathStationResponse(2L, "남부터미널역", LocalDateTime.now());
        PathStationResponse 양재역 = new PathStationResponse(3L, "양재역", LocalDateTime.now());
        List<PathStationResponse> pathStationResponses = new ArrayList<>();
        pathStationResponses.add(교대역);
        pathStationResponses.add(남부터미널역);
        pathStationResponses.add(양재역);
        PathResponse mockBody = new PathResponse(pathStationResponses, 5);

        return ResponseEntity.ok()
                .body(mockBody);
    }
}
