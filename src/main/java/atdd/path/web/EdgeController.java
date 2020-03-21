package atdd.path.web;

import atdd.path.domain.Edge;
import atdd.path.dto.EdgeRequestView;
import atdd.path.dto.EdgeResponseView;
import atdd.path.serivce.EdgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static atdd.path.PathConstant.BASE_URI_EDGE;

@RestController
@RequestMapping(value = BASE_URI_EDGE)
public class EdgeController {
    private EdgeService edgeService;

    public EdgeController(EdgeService edgeService) {
        this.edgeService = edgeService;
    }

    @PostMapping
    public ResponseEntity createEdge(@RequestBody EdgeRequestView requestView) {
        Edge savedEdge = edgeService.create(Edge.of(requestView));
        return ResponseEntity
                .created(URI.create(BASE_URI_EDGE + "/" + savedEdge.getId()))
                .body(EdgeResponseView.of(savedEdge));
    }

    @DeleteMapping()
    public ResponseEntity deleteStation(@RequestParam("lineId") Long lineId,
                                        @RequestParam("stationId") Long stationId) {
        List<Edge> oldEdges = edgeService.findEdgesByStationId(stationId);
        edgeService.deleteOldEdges(oldEdges);
        Edge edgeAfterDeleteStation = edgeService.createEdgeForMerge(lineId, stationId, oldEdges);
        return ResponseEntity
                .ok()
                .body(EdgeResponseView.of(edgeAfterDeleteStation));
    }
}