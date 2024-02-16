package nextstep.subway.line;

import nextstep.subway.line.section.SectionRequest;
import nextstep.subway.line.section.SectionResponse;
import nextstep.subway.path.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.createLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.showLines());
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.showLine(id));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody UpdateLineRequest updateLineRequest) {
        lineService.updateLine(id, updateLineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lines/{id}/sections")
    public ResponseEntity<LineSectionResponse> showLineSections(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.showLineSections(id));
    }

    @PostMapping("/lines/{id}/sections")
    public ResponseEntity<SectionResponse> addLineSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        SectionResponse sectionResponse = lineService.addSection(id, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + id + "/sections/" + sectionResponse.getId())).body(sectionResponse);
    }

    @DeleteMapping("/lines/{id}/sections")
    public ResponseEntity<Void> deleteLineSection(@PathVariable Long id, @RequestParam Long stationId) {
        lineService.deleteSection(id, stationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> shortestPath(@RequestParam("source") Long source, @RequestParam("target") Long target) {
        return ResponseEntity.ok().body(lineService.getShortestPath(source, target));
    }
}
