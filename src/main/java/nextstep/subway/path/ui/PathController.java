package nextstep.subway.path.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.path.application.PathService;
import nextstep.subway.path.application.dto.PathResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PathController {

    private final PathService pathService;

    @GetMapping("/paths")
    public PathResponse findPath(@RequestParam Long source, @RequestParam Long target) {
        return pathService.findPath(source, target);
    }
}
