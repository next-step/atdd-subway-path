package nextstep.subway.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/paths")
@RequiredArgsConstructor
public class PathController {

    private final PathService pathService;

    @GetMapping
    public ResponseEntity<PathResponse> findPath(@RequestParam("source") Long sourceId, @RequestParam("target") Long targetId) {

        return ResponseEntity.ok(pathService.findPath(sourceId, targetId));
    }

}
