package nextstep.subway.line.controller;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.dto.CreateLineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.UpdateLineRequest;
import nextstep.subway.line.repository.Line;
import nextstep.subway.line.service.LineService;
import nextstep.subway.section.dto.CreateSectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
class LineController {
    private final LineService lineService;

    @PostMapping("/lines")
    ResponseEntity<LineResponse> createLine(@RequestBody CreateLineRequest request) {
        LineResponse lineResponse = LineResponse.from(lineService.saveLine(request));
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @PostMapping("/lines/{id}/sections")
    ResponseEntity<LineResponse> createSection(@PathVariable Long id, @RequestBody CreateSectionRequest request) {
        LineResponse lineResponse = LineResponse.from(lineService.addSection(id, request));
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines")
    ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines().stream().map(LineResponse::from).collect(Collectors.toList()));
    }

    @GetMapping(value = "/lines/{id}")
    ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(LineResponse.from(lineService.findLineById(id)));
    }

    @PutMapping(value = "/lines/{id}")
    ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody UpdateLineRequest request) {
        lineService.updateLineById(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/lines/{id}")
    ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/lines/{id}/sections")
    ResponseEntity<LineResponse> deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        lineService.deleteSectionByStationId(id, stationId);
        return ResponseEntity.noContent().build();
    }
}
