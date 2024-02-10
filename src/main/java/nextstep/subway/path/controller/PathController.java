package nextstep.subway.path.controller;

import nextstep.subway.line.service.LineService;
import nextstep.subway.line.service.dto.LineCreateRequest;
import nextstep.subway.line.service.dto.LineResponse;
import nextstep.subway.path.service.PathService;
import nextstep.subway.path.service.dto.PathResponse;
import nextstep.subway.path.service.dto.PathSearchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PathController {

    private final PathService pathService;

    public PathController(final PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> findPath(final PathSearchRequest searchRequest) {
        final PathResponse pathResponse = pathService.findPath(searchRequest);
        return ResponseEntity.ok().body(pathResponse);
    }
}
