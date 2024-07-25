package nextstep.subway.ui;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.application.dto.LineRequest;
import nextstep.subway.application.dto.LineResponse;
import nextstep.subway.application.dto.SectionRequest;
import nextstep.subway.domain.service.LineCommandService;
import nextstep.subway.application.dto.SectionResponse;

@RestController
@RequestMapping("/lines")
public class LineCommandController {
    private final LineCommandService lineCommandService;

    public LineCommandController(LineCommandService lineCommandService) {
        this.lineCommandService = lineCommandService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineCommandService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create(getLineUriString(line))).body(line);
    }

    private String getLineUriString(LineResponse line) {
        return String.format("/lines/%s", line.getId().toString());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineCommandService.updateLine(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineCommandService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<SectionResponse> addSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        SectionResponse section = lineCommandService.addSection(lineId, sectionRequest);
        return ResponseEntity.created(URI.create(getSectionUriString(section))).body(section);
    }

    private String getSectionUriString(SectionResponse section) {
        return String.format("/lines/%s/sections", section.getLineId().toString());
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> removeSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        lineCommandService.removeSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}