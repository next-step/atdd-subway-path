package nextstep.subway.ui.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.line.LineService;
import nextstep.subway.applicaion.line.request.SectionRequest;
import nextstep.subway.applicaion.line.response.LineResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {
    private final LineService lineService;

    @PostMapping
    public ResponseEntity<LineResponse> appendSection(@PathVariable final Long lineId,
                                                      @RequestBody final SectionRequest request) {
        final var response = lineService.appendSection(lineId, request);
        return ResponseEntity
                .ok()
                .body(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeSection(@PathVariable final Long lineId, @RequestParam final Long stationId) {
        lineService.removeSection(lineId, stationId);
        return ResponseEntity
                .noContent()
                .build();
    }
}
