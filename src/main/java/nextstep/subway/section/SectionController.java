package nextstep.subway.section;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> createSection(final @PathVariable Long lineId, final @RequestBody SectionRequest sectionRequest) {
        SectionResponse response = sectionService.createSection(lineId, sectionRequest);

        return ResponseEntity.created(URI.create("/lines/" + lineId + "/stations/" + response.getId())).body(response);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(final @PathVariable Long lineId, final @RequestParam Long stationId) {
        sectionService.deleteSection(lineId, stationId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> findPath(final @RequestParam Long source, final @RequestParam Long target) {
        PathResponse response = sectionService.findPath(source, target);

        return ResponseEntity.ok(response);
    }
}
