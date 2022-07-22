package nextstep.subway.line.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.application.SectionService;
import nextstep.subway.line.application.dto.request.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lines")
public class SectionController {
    private final SectionService sectionService;

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<Void> addSection(@PathVariable Long lineId, @RequestBody @Valid SectionRequest sectionRequest) {
        sectionService.addSection(lineId, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.deleteSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
