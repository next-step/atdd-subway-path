package nextstep.subway.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Path;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;

    @GetMapping
    public PathResponse findPath(@Validated PathRequest request) {
        Path path = pathService.findShortestPath(request);
        return PathResponse.from(path);
    }

}
