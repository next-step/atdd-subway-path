package atdd.path.controller;

import atdd.path.api.response.PathResponseView;
import atdd.path.service.GraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/paths")
@RestController
public class PathController {

    private final GraphService graphService;

    @GetMapping("/distance")
    public ResponseEntity<PathResponseView> findDistancePath(@RequestParam Long startId, @RequestParam Long endId) {
        return ResponseEntity.ok(new PathResponseView(startId, endId, graphService.findDistancePath(startId, endId)));
    }

    @GetMapping("/time")
    public ResponseEntity<PathResponseView> findTimePath(@RequestParam Long startId, @RequestParam Long endId) {
        return ResponseEntity.ok(new PathResponseView(startId, endId, graphService.findTimePath(startId, endId)));
    }

}
