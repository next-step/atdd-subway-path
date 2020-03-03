package atdd.path.web;

import atdd.path.application.dto.*;
import atdd.path.domain.Line;
import atdd.path.domain.Station;
import atdd.path.service.EdgeService;
import atdd.path.service.LineService;
import atdd.path.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class LineController {
    private LineService lineService;
    private EdgeService edgeService;
    private StationService stationService;

    public LineController(LineService lineService, EdgeService edgeService,
                          StationService stationService) {
        this.lineService = lineService;
        this.edgeService = edgeService;
        this.stationService = stationService;
    }

    @PostMapping
    @RequestMapping(value = "/lines", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody LineRequestView requestView) {
        LineResponseView responseView = lineService.create(requestView);
        return ResponseEntity
                .created(URI.create("/lines/" + responseView.getId()))
                .body(responseView);
    }

    @DeleteMapping
    @RequestMapping("/lines/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping
    @RequestMapping("/lines/{id}")
    public ResponseEntity retrieve(@PathVariable Long id) {
        LineResponseView responseView = lineService.retrieve(id);
        return ResponseEntity
                .ok(responseView);
    }

    @GetMapping
    @RequestMapping("/lines")
    public ResponseEntity showAll() {
        LineListResponseView responseView = lineService.showAll();
        return ResponseEntity
                .ok(responseView.getLines());
    }
}
