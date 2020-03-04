package atdd.line.controller;

import atdd.line.api.request.CreateEdgeRequestView;
import atdd.line.api.request.CreateLineRequestView;
import atdd.line.api.response.LineResponseView;
import atdd.line.api.response.LinesResponseView;
import atdd.line.domain.Line;
import atdd.line.service.LineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static atdd.line.domain.Line.EMPTY_LINE;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/lines")
@RestController
public class LineController {

    public static final String LINE_URL = "/lines";
    public static final String EDGE_URL = "/edges";

    private final LineService lineService;

    @PostMapping
    public ResponseEntity<LineResponseView> createLine(@RequestBody CreateLineRequestView view) {
        final Line line = view.toLine();
        final Line persistLine = lineService.saveLine(line);

        return ResponseEntity
                .created(URI.create(LINE_URL + "/" + persistLine.getId()))
                .body(new LineResponseView(persistLine));
    }

    @GetMapping
    public ResponseEntity<LinesResponseView> getLines() {
        final List<Line> lines = lineService.findLineWithEdgeAll();
        return ResponseEntity.ok(new LinesResponseView(lines));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponseView> getLine(@PathVariable("id") Long id) {
        final Line findLine = lineService.findLineWithEdgeById(id).orElse(EMPTY_LINE);
        return ResponseEntity.ok(new LineResponseView(findLine));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteLine(@PathVariable("id") Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/edges")
    public ResponseEntity<LineResponseView> createEdge(@PathVariable("id") Long lineId,
                                                       @RequestBody CreateEdgeRequestView view) {
        final Line persistLine = lineService.saveEdge(lineId, view.getSourceStationId(), view.getTargetStationId(),
                view.getElapsedTime(), view.getDistance());

        return ResponseEntity
                .created(URI.create(LINE_URL + "/" + persistLine.getId()))
                .body(new LineResponseView(persistLine));
    }

    @DeleteMapping("/{lineId}/edges")
    public ResponseEntity<Object> deleteLineStation(@PathVariable("lineId") Long lineId,
                                                    @RequestParam("stationId") Long stationId) {

        lineService.deleteEdgeStation(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

}
