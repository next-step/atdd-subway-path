package nextstep.subway.path;

import nextstep.subway.exception.BadRequestException;
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
    public ResponseEntity<PathResponse> getPathInfo(@RequestParam Long source, @RequestParam Long target) {
        validate(source, target);
        var response = pathService.getShortestPath(source, target);
        return ResponseEntity.ok()
                .body(response);
    }

    private void validate(Long source, Long target) {
        if (source == target)
            throw new BadRequestException("source and target must be not same station.");
    }

}
