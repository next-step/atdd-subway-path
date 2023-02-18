package nextstep.subway.ui.lines;

import nextstep.subway.applicaion.dto.line.LineRequest;
import nextstep.subway.applicaion.dto.line.LineResponse;
import nextstep.subway.applicaion.line.LineCUDDoer;
import nextstep.subway.applicaion.line.LineFinder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private LineCUDDoer lineCUDDoder;
    private LineFinder lineFinder;

    public LineController(LineCUDDoer lineCUDDoder, LineFinder lineFinder) {
        this.lineCUDDoder = lineCUDDoder;
        this.lineFinder = lineFinder;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineCUDDoder.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> responses = lineFinder.showLines();
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        LineResponse lineResponse = lineFinder.findById(id);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineCUDDoder.updateLine(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id) {
        lineCUDDoder.deleteLine(id);
        return ResponseEntity.noContent().build();
    }
}
