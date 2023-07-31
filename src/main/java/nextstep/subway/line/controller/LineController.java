package nextstep.subway.line.controller;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.dto.request.SaveLineRequest;
import nextstep.subway.line.dto.request.SaveLineSectionRequest;
import nextstep.subway.line.dto.request.UpdateLineRequest;
import nextstep.subway.line.dto.response.LineResponse;
import nextstep.subway.line.service.LineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class LineController {

    private final LineService lineService;

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid SaveLineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);

        return ResponseEntity
                .created(URI.create(String.format("/lines/%d", line.getId())))
                .body(line);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok(lineService.findAllLines());
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.findLineById(id));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> updateLine(
            @PathVariable Long id,
            @RequestBody @Valid UpdateLineRequest lineRequest) {
        lineService.updateLine(id, lineRequest);
        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<LineResponse> saveLineSection(
            @PathVariable Long lineId,
            @RequestBody @Valid SaveLineSectionRequest lineSectionRequest) {
        LineResponse line = lineService.saveLineSection(lineId, lineSectionRequest);

        return ResponseEntity
                .created(URI.create(String.format("/lines/%d", line.getId())))
                .body(line);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteLineSection(
            @PathVariable Long lineId,
            @RequestParam Long stationId) {
        lineService.deleteLineSectionByStationId(lineId, stationId);
        return ResponseEntity
                .noContent()
                .build();
    }

}
