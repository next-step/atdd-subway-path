package nextstep.subway.ui;

import nextstep.subway.applicaion.GraphService;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {
    @Autowired
    private GraphService graphService;

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> path(@RequestParam Long source, @RequestParam Long target) {
        return ResponseEntity.ok().body(graphService.getShortestPath(source, target));
    }
}
