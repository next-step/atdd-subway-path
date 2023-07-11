package nextstep.subway.line.controller;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.service.LineManageService;
import nextstep.subway.line.service.LineReadService;
import nextstep.subway.line.view.LineCreateRequest;
import nextstep.subway.line.view.LineModifyRequest;
import nextstep.subway.line.view.LineResponse;


@RequestMapping("/lines")
@RestController
@RequiredArgsConstructor
public class LineController {
    private final LineManageService lineManageService;
    private final LineReadService lineReadService;

    @PostMapping
    public ResponseEntity<LineResponse> createLines(@RequestBody LineCreateRequest request) {
        LineResponse lineResponse = lineManageService.createLine(request);

        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        return ResponseEntity.ok(LineResponse.from(lineReadService.getLine(id)));
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        return ResponseEntity.ok(lineReadService.getList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyLine(@PathVariable Long id, @RequestBody LineModifyRequest request) {
        lineManageService.modifyLine(id, request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineManageService.deleteLine(id);

        return ResponseEntity.noContent().build();
    }
}
