package nextstep.subway.section;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> createStation(
            @PathVariable long lineId,
            @RequestBody SectionRequest sectionRequest) {

        return ResponseEntity.created(URI.create("/lines/" + lineId))
                .body(sectionService.createSection(lineId, sectionRequest));
    }

    @DeleteMapping("lines/{lineId}/sections/{stationId}")
    public ResponseEntity<Void> createStation(
            @PathVariable long lineId,
            @PathVariable long stationId) {
        sectionService.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
