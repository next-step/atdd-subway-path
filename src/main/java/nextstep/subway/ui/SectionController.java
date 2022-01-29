package nextstep.subway.ui;

import nextstep.subway.applicaion.SectionService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Section;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<Section> createSection(@PathVariable Long lineId,
                                                 @RequestBody SectionRequest request) {
        Section section = sectionService.createSection(lineId, request);
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections" + "?stationId=" + section.getId())).body(section);
    }

    @DeleteMapping
    public ResponseEntity<Section> deleteSection(@PathVariable Long lineId,
                                                 @RequestParam("stationId") Long stationId) {
        sectionService.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

}
