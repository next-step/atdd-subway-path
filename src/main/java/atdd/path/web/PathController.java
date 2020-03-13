package atdd.path.web;

import atdd.path.application.dto.PathResponseView;
import atdd.path.domain.Station;
import atdd.path.service.GraphService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/paths")
public class PathController {
    private GraphService graphService;

    public PathController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping
    public ResponseEntity findShortestPath(@RequestParam("startId") Long startId,
                                           @RequestParam("endId") Long endId) {
        List<Station> path = graphService.findStationsInShortestPath(startId, endId);
        PathResponseView responseView = PathResponseView.builder()
                .startId(startId)
                .endId(endId)
                .stations(path)
                .build();

        return ResponseEntity
                .ok(responseView);
    }
}
