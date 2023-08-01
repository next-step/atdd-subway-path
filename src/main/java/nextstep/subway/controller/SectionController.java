package nextstep.subway.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.service.LineService;

@RestController
public class SectionController {

    private final LineService lineService;

    public SectionController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<LineResponse> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        LineResponse lineResponse = lineService.addSection(lineId, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections")).body(lineResponse);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        lineService.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
