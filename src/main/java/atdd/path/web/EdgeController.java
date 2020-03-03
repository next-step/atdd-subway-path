package atdd.path.web;

import atdd.path.application.dto.*;
import atdd.path.domain.Line;
import atdd.path.domain.Station;
import atdd.path.service.EdgeService;
import atdd.path.service.LineService;
import atdd.path.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController(value = "/edges")
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
                                  @RequestBody EdgeRequestView edgeRequestView) throws Exception {
        LineResponseView lineResponseView = lineService.retrieve(requestViewFromClient.getLineId());
        Long sourceId = requestViewFromClient.getSourceId();
        Long targetId = requestViewFromClient.getTargetId();

        Station source = stationService.findById(StationRequestView.builder().id(sourceId).build());
        Station target = stationService.findById(StationRequestView.builder().id(targetId).build());
        Line line = Line.of(lineResponseView);

        edgeRequestView.insertLine(line);
        edgeRequestView.insertSource(source);
        edgeRequestView.insertTarget(target);
        EdgeResponseView edgeResponseView = edgeService.addEdge(edgeRequestView);

        return ResponseEntity
                .created(URI.create("/edges/" + edgeResponseView.getId()))
                .body(edgeResponseView);
    }
}
