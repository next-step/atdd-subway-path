package nextstep.subway.presentation;

import lombok.RequiredArgsConstructor;
import nextstep.subway.application.PathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/paths")
@RestController
public class PathController {

    private final PathService pathService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<PathResponse> createPath(@RequestParam Long source, @RequestParam Long target) {
        PathResponse pathResponse = pathService.getPath(source, target);
        return ResponseEntity.ok().body(pathResponse);
    }
}
