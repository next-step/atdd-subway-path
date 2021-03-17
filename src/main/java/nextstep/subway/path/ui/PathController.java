package nextstep.subway.path.ui;

import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.exception.SourceEqualsWithTargetException;
import nextstep.subway.path.exception.StationNotExistsException;
import nextstep.subway.path.exception.StationsNotConnectedException;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PathController {
    private PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @RequestMapping("/paths")
    public ResponseEntity getShortestPath(@RequestParam Long sourceId, @RequestParam Long targetId) {
        PathResponse response = pathService.getShortestPath(sourceId, targetId);
        return ResponseEntity.ok().body(response);
    }

    @ExceptionHandler({SourceEqualsWithTargetException.class, StationNotExistsException.class, StationsNotConnectedException.class})
    public ResponseEntity handlePathException(RuntimeException e) {
        return ResponseEntity.badRequest().build();
    }
}
