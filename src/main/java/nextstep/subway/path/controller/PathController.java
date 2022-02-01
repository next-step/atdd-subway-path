package nextstep.subway.path.controller;

import lombok.RequiredArgsConstructor;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.service.PathService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class PathController {
    private final PathService pathService;

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> searchPath(
            @RequestParam("source") Long sourceId,
            @RequestParam("target") Long targetId){
        return ResponseEntity.ok(pathService.findShortestPath(sourceId, targetId));
    }
}
