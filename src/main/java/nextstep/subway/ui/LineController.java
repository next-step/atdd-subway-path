package nextstep.subway.ui;

import nextstep.subway.applicaion.command.LineCommandService;
import nextstep.subway.applicaion.dto.*;
import nextstep.subway.applicaion.query.LineQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineCommandService lineCommandService;
    private final LineQueryService lineQueryService;

    public LineController(LineCommandService lineCommandService,
                          LineQueryService lineQueryService) {
        this.lineCommandService = lineCommandService;
        this.lineQueryService = lineQueryService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineCommandService.saveLine(lineRequest);

        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineAndSectionResponse>> showLine() {
        List<LineAndSectionResponse> lines = lineQueryService.findAllLines();

        return ResponseEntity.ok(lines);
    }

    @GetMapping("{id}")
    public ResponseEntity<LineAndSectionResponse> showLine(@PathVariable("id") Long id) {
        LineAndSectionResponse line = lineQueryService.findLine(id);

        return ResponseEntity.ok(line);
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> updateLine(@PathVariable("id") Long id,
                                           @RequestBody UpdateLineRequest request) {
        lineCommandService.updateLine(id, request);

        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable("id") Long id) {
        lineCommandService.deleteLine(id);

        return ResponseEntity.noContent()
                .build();
    }

    @PostMapping("{id}/sections")
    public ResponseEntity<LineAndSectionResponse> addSection(@PathVariable("id") Long id, @RequestBody SectionRequest request) {
        LineAndSectionResponse lineResponse = lineCommandService.addSection(id, request);

        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getLineId()))
                .body(lineResponse);
    }

    @DeleteMapping("{id}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable("id") Long lineId, @RequestParam("stationId") Long stationId) {
        lineCommandService.deleteSection(lineId, stationId);

        return ResponseEntity.noContent()
                .build();
    }

}
