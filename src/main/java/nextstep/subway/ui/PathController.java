package nextstep.subway.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/paths")
public class PathController {
    private final PathService pathService;

    @GetMapping
    public ResponseEntity<PathResponse> getShortestPath(@RequestParam Long source, @RequestParam Long target){
        PathResponse response = pathService.getShortestPath(source, target);
        return ResponseEntity.ok().body(response);
    }
}
