package nextstep.subway.ui.line;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.line.LineService;
import nextstep.subway.applicaion.line.request.LineCreateRequest;
import nextstep.subway.applicaion.line.request.LineUpdateRequest;
import nextstep.subway.applicaion.line.response.LineResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody final LineCreateRequest request) {
        final var response = lineService.saveLine(request);
        return ResponseEntity
                .created(URI.create("/Lines/" + response.getId()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity
                .ok()
                .body(lineService.findAllLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findLine(@PathVariable final Long id) {
        return ResponseEntity
                .ok()
                .body(lineService.findLine(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable final Long id, @RequestBody final LineUpdateRequest request) {
        lineService.updateLine(id, request);
        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long id) {
        lineService.deleteLine(id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
