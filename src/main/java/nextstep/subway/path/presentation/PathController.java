package nextstep.subway.path.presentation;

import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {
    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> showPath(@RequestParam Long source, @RequestParam Long target) {
        // source, target이 null로 들어오는 경우는 어떻게 처리할까?
        return ResponseEntity.ok().body(pathService.searchPath(source, target));
    }
}
