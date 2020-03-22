package atdd.path.web;

import atdd.path.domain.Line;
import atdd.path.dto.LineRequestView;
import atdd.path.dto.LineResponseView;
import atdd.path.serivce.LineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static atdd.path.PathConstant.BASE_URI_LINE;

@RestController
@RequestMapping(value = BASE_URI_LINE)
public class LineController {
    private LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequestView requestView) {
        Line savedLine = lineService.create(Line.of(requestView));
        return ResponseEntity
                .created(URI.create(BASE_URI_LINE + savedLine.getId()))
                .body(LineResponseView.of(savedLine));
    }

    @GetMapping("/{id}")
    public ResponseEntity retrieveLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        return ResponseEntity
                .ok()
                .body(LineResponseView.of(line));
    }

    @GetMapping
    public ResponseEntity showAllLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponseView> responseViews = lines.stream()
                .map(it -> LineResponseView.of(it))
                .collect(Collectors.toList());
        return ResponseEntity
                .ok()
                .body(responseViews);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity
                .noContent()
                .build();
    }
}