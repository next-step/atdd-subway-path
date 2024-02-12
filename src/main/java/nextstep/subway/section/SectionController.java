package nextstep.subway.section;

import java.net.URI;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SectionController {
    private SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }
    @PostMapping("/lines/{id}/sections")
    public ResponseEntity<LineResponse> createSection(@PathVariable long id, @RequestBody SectionRequest sectionRequest) {
        Line line = sectionService.save(id, sectionRequest);

        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(
                new LineResponse(line));
    }

    @DeleteMapping("lines/{lineId}/sections/{stationId}")
    public ResponseEntity<Void> deleteSection(@PathVariable long lineId, @PathVariable long stationId) {
        sectionService.deleteSectionByLineId(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
