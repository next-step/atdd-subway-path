package nextstep.subway.path;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/paths")
@RestControllerAdvice
public class PathController {
    private final PathService pathService;

    public PathController(final PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping(params = {"source", "target"})
    public PathResponse findPath(final Long source, final Long target) {
        return pathService.findPath(source, target);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException(IllegalArgumentException e) {

    }
}
