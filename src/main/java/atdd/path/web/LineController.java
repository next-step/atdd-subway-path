package atdd.path.web;

import atdd.path.application.dto.LineRequestView;
import atdd.path.application.dto.LineResponseView;
import atdd.path.service.EdgeService;
import atdd.path.service.LineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private LineService lineService;
    private EdgeService edgeService;

    public LineController(LineService lineService, EdgeService edgeService) {
        this.lineService = lineService;
        this.edgeService = edgeService;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody LineRequestView requestView) {
        LineResponseView responseView = lineService.create(requestView);
        return ResponseEntity
                .created(URI.create("/lines/" + responseView.getId()))
                .body(responseView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity retrieve(@PathVariable Long id) {
        LineResponseView responseView = lineService.retrieve(id);
        return ResponseEntity
                .ok(responseView);
    }

    @GetMapping
    public ResponseEntity showAll() {
        List<LineResponseView> responseView = lineService.showAll();
        return ResponseEntity
                .ok(responseView);
    }
}
