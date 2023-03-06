package nextstep.subway.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PathController {

    private final PathService pathService;

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> findPath(@RequestParam Long source, @RequestParam Long target) {
        PathResponse response = pathService.findPath(source, target);
        return ResponseEntity.ok().body(response);
    }
}
