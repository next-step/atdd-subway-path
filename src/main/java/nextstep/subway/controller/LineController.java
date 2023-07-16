package nextstep.subway.controller;

import nextstep.subway.controller.request.LineCreateRequest;
import nextstep.subway.controller.request.LineModifyRequest;
import nextstep.subway.controller.request.SectionAddRequest;
import nextstep.subway.controller.resonse.LineResponse;
import nextstep.subway.service.LineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequestMapping("/lines")
@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineCreateRequest lineCreateRequest) {
        LineResponse lineResponse = lineService.saveLine(lineCreateRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findLineById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyLine(@PathVariable Long id, @RequestBody LineModifyRequest lineModifyRequest) {
        lineService.modifyLine(id, lineModifyRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<Void> addLineSection(@PathVariable Long id, @RequestBody SectionAddRequest subwayLineCreateRequest) {
        lineService.addSection(id, subwayLineCreateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> deleteStationAtLineSection(@PathVariable Long id, @RequestParam Long stationId) {
        lineService.deleteStationAtLine(id, stationId);
        return ResponseEntity.noContent().build();
    }
}
