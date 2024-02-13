package nextstep.subway.line;

import nextstep.subway.line.section.Section;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nextstep.subway.line.section.SectionRequest;
import nextstep.subway.line.section.SectionResponse;
import nextstep.subway.line.section.SectionService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines().stream().map(LineResponse::of).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(LineResponse.of(lineService.findLineById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLineById(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<SectionResponse> createSection(
            @PathVariable Long lineId,
            @RequestBody SectionRequest request) {
        Section section = sectionService.addSection(lineId, request);
        SectionResponse response = SectionResponse.of(section);
        return ResponseEntity.created(URI.create("/sections/" + response.getId())).body(response);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(
            @PathVariable Long lineId,
            @Param("stationId") Long stationId) {
        sectionService.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineId}/stations")
    public ResponseEntity<LineResponse> addStationInLine(
            @PathVariable Long lineId,
            @RequestBody StationInLineRequest request) {
        Line line = sectionService.addStationInLine(lineId, request);
        LineResponse response = LineResponse.of(line);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }
}
