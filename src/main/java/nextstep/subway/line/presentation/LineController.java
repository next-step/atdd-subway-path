package nextstep.subway.line.presentation;

import nextstep.subway.line.presentation.request.AddSectionRequest;
import nextstep.subway.line.presentation.request.CreateLineRequest;
import nextstep.subway.line.presentation.request.UpdateLineRequest;
import nextstep.subway.line.presentation.response.*;
import nextstep.subway.line.service.LineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class LineController {
    private LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<CreateLineResponse> createLine(@RequestBody CreateLineRequest createLineRequest) {
        CreateLineResponse line = lineService.saveLine(createLineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getLineId())).body(line);
    }

    @GetMapping(value = "/lines")
    public ResponseEntity<ShowAllLinesResponse> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<ShowLineResponse> showLine(@PathVariable Long lineId) {
        ShowLineResponse line = lineService.findLine(lineId);
        return ResponseEntity.ok().body(line);
    }

    @PutMapping("/lines/{lineId}")
    public ResponseEntity<UpdateLineResponse> updateLine(@PathVariable Long lineId, @RequestBody UpdateLineRequest updateLineRequest) {
        UpdateLineResponse line = lineService.updateLine(lineId, updateLineRequest);
        return ResponseEntity.ok().body(line);
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long lineId) {
        lineService.deleteLine(lineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{lineId}/section")
    public ResponseEntity<AddSectionResponse> addSection(@PathVariable Long lineId, @RequestBody AddSectionRequest addSectionRequest) {
        AddSectionResponse line = lineService.addSection(lineId, addSectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getLineId() + "section")).body(line);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        lineService.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

}
