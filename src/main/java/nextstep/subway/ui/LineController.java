package nextstep.subway.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.LineCommandService;
import nextstep.subway.applicaion.LineQueryService;
import nextstep.subway.applicaion.StationQueryService;
import nextstep.subway.applicaion.dto.LineCreationRequest;
import nextstep.subway.applicaion.dto.LineModificationRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRegistrationRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LineController {

    private final LineQueryService lineQueryService;
    private final LineCommandService lineCommandService;


    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineCreationRequest lineRequest) {
        LineResponse lineResponse = lineCommandService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId()))
                .body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLine() {
        return ResponseEntity.ok()
                .body(lineQueryService.findAllLines());
    }

    @GetMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(lineQueryService.findLine(id));
    }

    @PutMapping(value = "/lines/{id}")
    public ResponseEntity<Void> modifyLine(
            @PathVariable Long id,
            @RequestBody @Valid LineModificationRequest lineRequest) {
        lineCommandService.modifyLine(id, lineRequest);
        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineCommandService.deleteLineById(id);
        return ResponseEntity.noContent()
                .build();
    }

    @PostMapping("/lines/{id}/sections")
    public ResponseEntity<LineResponse> addSection(
            @PathVariable long id,
            @RequestBody @Valid SectionRegistrationRequest sectionRequest) {
        lineCommandService.addSection(id, sectionRequest);
        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/lines/{id}/sections")
    public ResponseEntity<Void> removeSection(@PathVariable Long id, @RequestParam("stationId") Long stationId) {
        lineCommandService.removeSection(id, stationId);
        return ResponseEntity.noContent()
                .build();
    }

}
