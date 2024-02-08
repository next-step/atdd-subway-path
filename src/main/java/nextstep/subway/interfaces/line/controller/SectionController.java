package nextstep.subway.interfaces.line.controller;

import nextstep.subway.interfaces.line.dto.LineRequest;
import nextstep.subway.interfaces.line.dto.LineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nextstep.subway.domain.line.LineCommand;
import nextstep.subway.domain.line.LineInfo;
import nextstep.subway.domain.line.service.SectionService;

import java.net.URI;

@RestController
@RequestMapping("/lines/{line-id}/sections")
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createSection(@PathVariable("line-id") Long lineId, @RequestBody LineRequest.Section request) {
        LineInfo.Main line = sectionService.saveSection(LineCommand.SectionAddCommand.of(lineId, request));
        LineResponse response = LineResponse.from(line);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(response);
    }

    @DeleteMapping
    public ResponseEntity<LineResponse> deleteSection(@PathVariable("line-id") Long lineId, @RequestParam("stationId") Long stationId) {
        LineInfo.Main line = sectionService.removeSection(LineCommand.SectionDeleteCommand.of(lineId, stationId));
        return ResponseEntity.noContent().build();
    }
}
