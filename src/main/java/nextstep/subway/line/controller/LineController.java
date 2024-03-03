package nextstep.subway.line.controller;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nextstep.subway.line.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(final @RequestBody LineRequest request) {
        final LineResponse line = lineService.createLine(request);

        return ResponseEntity.created(URI.create("/line/" + line.getId())).body(line);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> getLines() {
        final List<LineResponse> lines = lineService.getLines();

        return ResponseEntity.ok(lines);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> getLine(final @PathVariable Long id) {
        final LineResponse line = lineService.getLine(id);

        return ResponseEntity.ok(line);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> updateLine(final @PathVariable Long id, final @RequestBody LineUpdateRequest request) {
        lineService.updateLine(id, request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(final @PathVariable Long id) {
        lineService.deleteLine(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{id}/sections")
    public ResponseEntity<LineResponse> createLineSection(final @PathVariable Long id, final @RequestBody SectionRequest request) {
        final LineResponse line = lineService.createLineSection(id, request);

        return ResponseEntity.created(URI.create("/line/" + line.getId())).body(line);
    }

    @DeleteMapping("/lines/{id}/sections")
    public ResponseEntity<Void> deleteLineSection(final @PathVariable("id") Long lineId, @RequestParam("stationId") Long stationId) {
        lineService.deleteLineSection(lineId, stationId);

        return ResponseEntity.noContent().build();
    }

}
