package nextstep.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import nextstep.subway.dto.PathResponse;
import nextstep.subway.service.PathService;

@RestController
@AllArgsConstructor
public class PathController {
    private final PathService pathService;

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> showPath(@RequestParam Long sourceId, @RequestParam Long targetId) {
        return ResponseEntity.ok().body(pathService.getShortestPath(sourceId, targetId));
    }

}
