package nextstep.subway.line;

import lombok.RequiredArgsConstructor;
import nextstep.subway.section.SectionAddRequest;
import nextstep.subway.section.SectionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    @PostMapping
    public ResponseEntity<LineResponse> create(@RequestBody LineCreateRequest request) {
        LineResponse line = lineService.create(request);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> update(@PathVariable Long id, @RequestBody LineUpdateRequest request) {
        return ResponseEntity.ok().body(lineService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<SectionResponse> addSection(@PathVariable("id") Long lineId, @RequestBody SectionAddRequest request) {
        return ResponseEntity.created(URI.create("/lines/")).body(lineService.addSection(lineId, request));
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable("id") Long lineId, @RequestParam("stationId") Long stationId) {
        lineService.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
