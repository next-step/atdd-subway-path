package nextstep.subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nextstep.subway.line.addsection.LineAddSectionRequest;
import nextstep.subway.line.addsection.LineAddSectionService;
import nextstep.subway.line.addsection.LineAddedSectionResponse;
import nextstep.subway.line.create.LineCreateRequest;
import nextstep.subway.line.create.LineCreateService;
import nextstep.subway.line.create.LineCreatedResponse;
import nextstep.subway.line.delete.LineDeleteService;
import nextstep.subway.line.load.LineLoadService;
import nextstep.subway.line.load.LineLoadedResponse;
import nextstep.subway.line.removeSection.LineRemoveSectionService;
import nextstep.subway.line.update.LineUpdateRequest;
import nextstep.subway.line.update.LineUpdateService;

import java.net.URI;
import java.util.List;

@RequestMapping("/lines")
@RestController
public class LineController {

    private final LineCreateService lineCreateService;
    private final LineLoadService lineLoadService;
    private final LineUpdateService lineUpdateService;
    private final LineDeleteService lineDeleteService;
    private final LineAddSectionService lineAddSectionService;
    private final LineRemoveSectionService lineRemoveSectionService;

    public LineController(LineCreateService lineCreateService, LineLoadService lineLoadService, LineUpdateService lineUpdateService, LineDeleteService lineDeleteService, LineAddSectionService lineAddSectionService, LineRemoveSectionService lineRemoveSectionService) {
        this.lineCreateService = lineCreateService;
        this.lineLoadService = lineLoadService;
        this.lineUpdateService = lineUpdateService;
        this.lineDeleteService = lineDeleteService;
        this.lineAddSectionService = lineAddSectionService;
        this.lineRemoveSectionService = lineRemoveSectionService;
    }

    @PostMapping
    public ResponseEntity<LineCreatedResponse> createLine(@RequestBody LineCreateRequest request) {
        LineCreatedResponse response = lineCreateService.createLine(request);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LineLoadedResponse>> getLine() {
        List<LineLoadedResponse> responses = lineLoadService.loadLines();
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/{line-id}")
    public ResponseEntity<LineLoadedResponse> getLine(@PathVariable("line-id") Long lineId) {
        LineLoadedResponse response = lineLoadService.loadLine(lineId);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{line-id}")
    public ResponseEntity<Void> updateLine(@PathVariable("line-id") Long lineId, @RequestBody LineUpdateRequest request) {
        lineUpdateService.updateLine(lineId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{line-id}")
    public ResponseEntity<Void> deleteLine(@PathVariable("line-id") Long lineId) {
        lineDeleteService.deleteLine(lineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{line-id}/sections")
    public ResponseEntity<LineAddedSectionResponse> addSection(@PathVariable("line-id") Long lineId, @RequestBody LineAddSectionRequest request) {
        LineAddedSectionResponse response = lineAddSectionService.addSection(lineId, request);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }

    @DeleteMapping("/{line-id}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable("line-id") Long lineId, @RequestParam("stationId") Long stationdId) {
        lineRemoveSectionService.deleteSection(lineId, stationdId);
        return ResponseEntity.noContent().build();
    }
}
