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
    public ResponseEntity<LineResponse> createStationLine(@RequestBody LineCreateRequest lineCreateRequest) {
        LineResponse lineResponse = lineService.saveStationLine(lineCreateRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showStationLines() {
        return ResponseEntity.ok().body(lineService.findAllSubwayLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showStationLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findSubwayLine(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyStationLine(@PathVariable Long id, @RequestBody LineModifyRequest lineModifyRequest) {
        lineService.modifySubwayLine(id, lineModifyRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStationLine(@PathVariable Long id) {
        lineService.deleteSubwayLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<Void> addStationLineSection(@PathVariable Long id, @RequestBody SectionAddRequest subwayLineCreateRequest) {
        lineService.addSection(id, subwayLineCreateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> deleteStationAtLineSection(@PathVariable Long id, @RequestParam Long stationId) {
        lineService.deleteStationAtLineSection(id, stationId);
        return ResponseEntity.noContent().build();
    }
}
