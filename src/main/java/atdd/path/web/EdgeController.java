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
@RequestMapping(value = "/edges")
public class EdgeController {
    private LineService lineService;
    private EdgeService edgeService;
    private StationService stationService;

    public EdgeController(LineService lineService, EdgeService edgeService,
                          StationService stationService) {
        this.lineService = lineService;
        this.edgeService = edgeService;
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity addEdge(@RequestBody EdgeRequestViewFromClient requestViewFromClient,
                                  EdgeRequestView edgeRequestView) throws Exception {

        return ResponseEntity
                .ok()
                .build();
    }
}
