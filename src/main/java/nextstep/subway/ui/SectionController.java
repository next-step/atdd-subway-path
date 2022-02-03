package nextstep.subway.ui;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.SectionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    private LineService lineService;

    public SectionController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long lineId,
                                                         @RequestBody SectionRequest sectionRequest) {
        SectionResponse section = lineService.saveSection(sectionRequest, lineId);
        return ResponseEntity.created(URI.create("lines/" + lineId + "/sections/" + section.getId()))
                .body(section);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        lineService.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
