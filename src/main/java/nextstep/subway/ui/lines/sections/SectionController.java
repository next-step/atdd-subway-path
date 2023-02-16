package nextstep.subway.ui.lines.sections;

import nextstep.subway.applicaion.dto.section.SectionRequest;
import nextstep.subway.applicaion.line.sections.LineSectionsCUDDoder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lines")
public class SectionController {
    private LineSectionsCUDDoder lineSectionsCUDDoder;

    public SectionController(LineSectionsCUDDoder lineSectionsCUDDoder) {
        this.lineSectionsCUDDoder = lineSectionsCUDDoder;
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<Void> addSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        lineSectionsCUDDoder.addSection(lineId, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        lineSectionsCUDDoder.deleteSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
