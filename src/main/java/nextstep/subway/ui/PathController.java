package nextstep.subway.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PathController {

    private final PathService pathService;

    @GetMapping(value = "/paths", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PathResponse> getShortestPath(@RequestParam Long source, @RequestParam Long target) {
        return ResponseEntity.ok()
                .body(pathService.getShortestPath(source, target));
    }

}
