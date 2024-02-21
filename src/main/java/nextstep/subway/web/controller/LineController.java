package nextstep.subway.web.controller;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nextstep.subway.web.dto.request.AddSectionRequest;
import nextstep.subway.web.dto.request.LineCreateRequest;
import nextstep.subway.web.dto.request.LineUpdateRequest;
import nextstep.subway.web.dto.response.LineResponse;
import nextstep.subway.web.service.LineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/lines")
@RestController
@RequiredArgsConstructor
public class LineController {

    private final LineService lineService;


    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineCreateRequest lineCreateRequest) {
        LineResponse lineResponse = lineService.saveLine(lineCreateRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok(lineService.findAllLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(lineService.findLine(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(
        @PathVariable Long id,
        @RequestBody LineUpdateRequest lineUpdateRequest
    ) {
        lineService.updateLine(id, lineUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LineResponse> deleteLine(
        @PathVariable Long id
    ) {
        lineService.removeLine(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<LineResponse> addSection(
        @PathVariable Long id,
        @RequestBody AddSectionRequest addSectionRequest
    ) {
        LineResponse lineResponse = lineService.addSection(id, addSectionRequest);
        return ResponseEntity.ok(lineResponse);
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<LineResponse> removeSection(
        @PathVariable Long id,
        @RequestParam(name = "stationId") Long stationId
    ) {
        LineResponse lineResponse = lineService.removeSection(id, stationId);
        return ResponseEntity.ok(lineResponse);
    }

}
