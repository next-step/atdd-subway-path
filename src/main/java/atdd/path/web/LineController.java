package atdd.path.web;

import atdd.path.application.dto.*;
import atdd.path.domain.Edge;
import atdd.path.domain.Line;
import atdd.path.domain.Station;
import atdd.path.service.EdgeService;
import atdd.path.service.LineService;
import atdd.path.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/lines")
public class LineController {
    private LineService lineService;
    private EdgeService edgeService;
    private StationService stationService;

    public LineController(LineService lineService, EdgeService edgeService,
                          StationService stationService) {
        this.lineService = lineService;
        this.edgeService = edgeService;
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody LineRequestView requestView) {
        LineResponseView responseView = lineService.create(requestView);
        return ResponseEntity
                .created(URI.create("/lines/" + responseView.getId()))
                .body(responseView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity retrieve(@PathVariable Long id) {
        LineResponseView responseView = lineService.retrieve(id);
        return ResponseEntity
                .ok(responseView);
    }

    @GetMapping
    public ResponseEntity showAll() {
        LineListResponseView responseView = lineService.showAll();
        return ResponseEntity
                .ok(responseView.getLines());
    }

    @PostMapping("/{id}/edges")
    public ResponseEntity createEdge(@PathVariable Long id,
                                     @RequestBody EdgeRequestView requestView) throws Exception {
        Edge edge = edgeService.addEdge(requestView);
        LineResponseView lineResponseView = lineService.retrieve(edge.getLineId());
        Line line = Line.of(lineResponseView);

        Station sourceStation = stationService.findById(StationRequestView.builder()
                .id(edge.getSourceId())
                .build());
        sourceStation.addLine(line);
        Station targetStation = stationService.findById(StationRequestView.builder()
                .id(edge.getTargetId())
                .build());
        targetStation.addLine(line);

        line.getStations()=lineService.
        EdgeResponseView edgeResponseView = EdgeResponseView.builder()
                .id(edge.getId())
                .line(line)
                .source(sourceStation)
                .target(targetStation)
                .build();

        return ResponseEntity
                .created(URI.create("/lines/"+id+"/"+edge.getId()))
                .body(edgeResponseView);
    }
}
