package atdd.edge;


import atdd.line.Line;
import atdd.line.LineService;
import atdd.station.Station;
import atdd.station.StationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(value = EdgeLink.ROOT)
@RestController
public class EdgeController {

    private final EdgeService edgeService;
    private final StationServiceImpl stationService;
    private final LineService lineService;

    @PostMapping
    public ResponseEntity create(@RequestBody EdgeDto.Request request) {

        Line line = lineService.findBy(request.getLineId());

        Station sourceStation = stationService.findBy(request.getSourceStationId());
        Station targetStation = stationService.findBy(request.getTargetStationId());

        Edge createdEdge = edgeService.create(request.toEntity(line, sourceStation, targetStation));

        return ResponseEntity.created(EdgeLink.getCreatedUrl(createdEdge.getId())).contentType(MediaType.APPLICATION_JSON).build();
    }
}
