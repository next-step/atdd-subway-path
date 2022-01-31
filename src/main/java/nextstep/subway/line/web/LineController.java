package nextstep.subway.line.web;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.service.LineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest request) {
        LineResponse line = lineService.saveLine(request);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok(lineService.findAllLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable("id") Long lineId) {
        return ResponseEntity.ok(lineService.findLine(lineId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> modifyLine(@PathVariable("id") Long lineId,
                                                   @RequestBody LineRequest request) {
        return ResponseEntity.ok(lineService.updateLine(lineId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable("id") Long lineId) {
        lineService.deleteLineById(lineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<Void> addSection(
            @RequestBody SectionRequest request,
            @PathVariable("id") Long lineId) {
        lineService.addSection(lineId, request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> removeSectionByStationId(
            @PathVariable("id") Long lindId,
            @RequestParam Long stationId) {
        lineService.removeSection(lindId, stationId);
        return ResponseEntity.noContent().build();
    }

}
