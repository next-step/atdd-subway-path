package atdd.station.web;

import atdd.station.application.StationCommandService;
import atdd.station.application.StationQueryService;
import atdd.station.application.dto.StationResponseDto;
import atdd.station.domain.Station;
import atdd.station.web.dto.StationCreateRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(StationController.ROOT_URI)
public class StationController {
    private static final Logger logger = LoggerFactory.getLogger(StationController.class);

    public static final String ROOT_URI = "/stations";

    private StationCommandService stationCommandService;
    private StationQueryService stationQueryService;

    public StationController(StationCommandService stationCommandService,
                             StationQueryService stationQueryService) {

        this.stationCommandService = stationCommandService;
        this.stationQueryService = stationQueryService;
    }

    @PostMapping
    public ResponseEntity<Void> createStation(@RequestBody StationCreateRequestDto stationCreateRequest) {
        logger.info("[StationController.createStation] stationCreateRequest={}", stationCreateRequest);

        Station savedStation = stationCommandService.create(stationCreateRequest.getName());

        return ResponseEntity.created(URI.create(StationController.ROOT_URI + "/" + savedStation.getId())).build();
    }

    @GetMapping
    public ResponseEntity<List<StationResponseDto>> getStations() {
        return ResponseEntity.ok().body(stationQueryService.getStations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationResponseDto> getStation(@PathVariable Long id) {
        logger.info("[StationController.getStation] id={}", id);

        StationResponseDto result = stationQueryService.getStation(id);

        logger.info("[StationController.getStation] result={}", result);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        logger.info("[StationController.deleteStation] id={}", id);

        stationCommandService.deleteStation(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.LOCATION, StationController.ROOT_URI)
                .build();
    }

}
