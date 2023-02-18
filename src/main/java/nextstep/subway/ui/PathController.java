package nextstep.subway.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/paths")
@RestController
public class PathController {

    private final PathService pathService;

    @GetMapping
    public ResponseEntity<PathResponse> getShortestPath(@ModelAttribute PathRequest pathRequest) {
        PathResponse pathResponse = pathService.getShortestPath(pathRequest);
        return ResponseEntity.ok().body(pathResponse);
    }
}
