package atdd.line.controller;

import atdd.line.api.request.CreateLineRequestView;
import atdd.line.api.response.LineListResponseView;
import atdd.line.api.response.LineResponseView;
import atdd.line.domain.Line;
import atdd.line.service.LineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/lines")
@RestController
public class LineController {

    private final LineService lineService;

    @PostMapping
    public ResponseEntity<LineResponseView> createLine(@RequestBody CreateLineRequestView view, HttpServletRequest request) {
        final Line line = view.toLine();
        final Line persistLine = lineService.save(line);

        return ResponseEntity
                .created(URI.create(request.getServletPath() + "/" + persistLine.getId()))
                .body(new LineResponseView(persistLine));
    }

    @GetMapping
    public ResponseEntity<LineListResponseView> getLines() {
        final List<Line> lines = lineService.findAll();
        final List<LineResponseView> views = lines.stream().map(LineResponseView::new).collect(toList());

        return ResponseEntity.ok(new LineListResponseView(lines.size(), views));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponseView> getLine(@PathVariable("id") Long id) {
        final Line line = lineService.findLineById(id);
        return ResponseEntity.ok(new LineResponseView(line));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteLine(@PathVariable("id") Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

}
