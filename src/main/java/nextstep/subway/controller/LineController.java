package nextstep.subway.controller;

import nextstep.subway.domain.request.LineRequest;
import nextstep.subway.domain.request.SectionRequest;
import nextstep.subway.domain.response.LineResponse;
import nextstep.subway.domain.response.SectionResponse;
import nextstep.subway.service.LineService;
import nextstep.subway.service.SectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping("")
    public ResponseEntity<LineResponse> createSubwayLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping(value = "")
    public ResponseEntity<List<LineResponse>> showSubwayLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<LineResponse> showSubwayLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findLineById(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<LineResponse> updateSubwayLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        return ResponseEntity.ok().body(lineService.updateLine(id, lineRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubwayLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable("id") Long id, @RequestBody SectionRequest sectionRequest) {
        LineResponse line = lineService.addSection(id, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + id)).body(line);
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable("id") Long id, @RequestParam("stationId") Long stationId) {
        sectionService.deleteSection(id, stationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/sections/{sectionId}")
    public ResponseEntity<SectionResponse> showSection(@PathVariable("id") Long id, @PathVariable("sectionId") Long sectionId) {
        return ResponseEntity.ok().body(lineService.findSection(id, sectionId));
    }
}
