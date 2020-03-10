package atdd.path.web;

import atdd.path.application.dto.EdgeRequestViewFromClient;
import atdd.path.application.dto.EdgeResponseView;
import atdd.path.domain.Edge;
import atdd.path.service.EdgeService;
import atdd.path.service.LineService;
import atdd.path.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/edges")
public class EdgeController {
    private EdgeService edgeService;

    public EdgeController(EdgeService edgeService) {
        this.edgeService = edgeService;
    }

    @PostMapping("/{lineId}")
    public ResponseEntity addEdge(@PathVariable("lineId") Long lineId,
                                  @RequestBody EdgeRequestViewFromClient request) throws Exception {

        Edge edge = edgeService.addEdge(lineId, request.getSourceId(), request.getTargetId(), request.getDistance());
        return ResponseEntity
                .ok()
                .body(EdgeResponseView.of(edge));
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity deleteStation(@PathVariable("lineId") Long lineId,
                                        @RequestParam("stationId") Long stationId) {
        edgeService.deleteEdgeByStationId(lineId, stationId);
        return ResponseEntity
                .ok()
                .build();
    }
}
