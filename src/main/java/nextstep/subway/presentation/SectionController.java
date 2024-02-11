package nextstep.subway.presentation;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class SectionController {

    private final LineService lineService;

    public SectionController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> createLine(@PathVariable Long lineId,
                                                      @RequestBody SectionRequest request) {
        SectionResponse sectionResponse =
                lineService.createSection(SectionRequest.mergeForCreateLine(lineId, request));
        return ResponseEntity.created(
                URI.create(String.format("/lines/%d/sections", lineId))).body(sectionResponse);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteLine(@PathVariable Long lineId,
                                           @RequestParam(name = "stationId") Long stationIdToDelete) {
        lineService.deleteSection(lineId, stationIdToDelete);
        return ResponseEntity.noContent().build();
    }
}
