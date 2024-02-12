package nextstep.subway.controller;

import nextstep.subway.dto.line.LineRequest;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.line.LineUpdateRequest;
import nextstep.subway.dto.section.SectionRequest;
import nextstep.subway.service.LineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest request) {
        LineResponse response = lineService.createLine(request);

        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> responses = lineService.getLines();

        return ResponseEntity
                    .ok()
                    .body(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        LineResponse response = lineService.getLine(id);

        return ResponseEntity
                    .ok()
                    .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyLine(
        @PathVariable Long id,
        @RequestBody LineUpdateRequest request
    ) {
        lineService.modifyLine(id, request);

        return ResponseEntity
                    .ok()
                    .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);

        return ResponseEntity
                    .noContent()
                    .build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<Long> createSection(
        @PathVariable("id") Long lineId,
        @RequestBody SectionRequest sectionRequest
    ) {
        Long sectionId = lineService.createSection(lineId, sectionRequest);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(sectionId);
    }

    @DeleteMapping("/{id}/sections")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSection(
        @PathVariable("id") Long lineId,
        @RequestParam Long stationId
    ) {
        lineService.deleteSection(lineId, stationId);
    }
}
