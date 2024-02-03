package nextstep.subway.ui;

import nextstep.subway.application.dto.SectionRequest;
import nextstep.subway.application.SectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SectionController {
    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        sectionService.saveSection(lineId, sectionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity deleteSection(@RequestParam Long stationId, @PathVariable Long lineId) {
        sectionService.deleteSection(stationId, lineId);
        return ResponseEntity.noContent().build();
    }
}
