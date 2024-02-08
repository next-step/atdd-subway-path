package nextstep.subway.interfaces.line.controller;

import nextstep.subway.interfaces.line.dto.LineRequest;
import nextstep.subway.interfaces.line.dto.LineResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nextstep.subway.domain.line.LineInfo;
import nextstep.subway.interfaces.line.dto.LinePatchRequest;
import nextstep.subway.domain.line.service.LineService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest.Line request) {
        LineInfo.Main line = lineService.saveLine(request);
        LineResponse response = LineResponse.from(line);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineInfo.Main> lines = lineService.listAll();
        List<LineResponse> response = lines.stream().map(LineResponse::from).collect(Collectors.toList());

        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> retrieveLine(@PathVariable Long id) {
        LineInfo.Main line = lineService.retrieveBy(id);
        LineResponse response = LineResponse.from(line);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateLine(@PathVariable Long id, @RequestBody LinePatchRequest request) {
        lineService.updateBy(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLine(@PathVariable Long id) {
        lineService.deleteBy(id);
        return ResponseEntity.noContent().build();
    }
}
