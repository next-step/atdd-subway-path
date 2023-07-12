package nextstep.subway.section.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import nextstep.subway.line.application.LineService;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;

@RequestMapping("/lines/{lineId}")
@RestController
public class SectionController {
    private final LineService lineService;

    public SectionController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/sections")
    public ResponseEntity<SectionResponse> registerSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        return ResponseEntity.ok().body(lineService.registerSection(lineId, sectionRequest));
    }

    @DeleteMapping("/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        lineService.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
