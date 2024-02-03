package nextstep.subway.line.controller;

import nextstep.subway.line.service.LineService;
import nextstep.subway.line.service.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody final LineCreateRequest createRequest) {
        final LineResponse lineResponse = lineService.saveLine(createRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable final Long id) {
        return ResponseEntity.ok().body(lineService.findLineById(id));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> modifyLine(@PathVariable final Long id, @RequestBody final LineUpdateRequest updateRequest) {
        lineService.updateLine(id, updateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<LineResponse> removeLine(@PathVariable final Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> registerSection(@PathVariable final Long lineId, @RequestBody final SectionCreateRequest createRequest) {
        final SectionResponse sectionResponse = lineService.addSection(lineId, createRequest);
        return ResponseEntity.created(URI.create(String.format("/lines/%s/sections/%s", lineId, sectionResponse.getId()))).body(sectionResponse);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> removeSection(@PathVariable final Long lineId, @RequestParam("stationId") final Long stationId) {
        lineService.removeSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

}
