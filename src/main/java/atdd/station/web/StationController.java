package atdd.station.web;

import atdd.station.application.StationCommandService;
import atdd.station.application.StationQueryService;
import atdd.station.domain.Station;
import atdd.station.web.dto.StationCreateRequestDto;
import atdd.station.web.dto.StationResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class StationController {
    private static final Logger logger = LoggerFactory.getLogger(StationController.class);

    private StationCommandService stationCommandService;
    private StationQueryService stationQueryService;

    public StationController(StationCommandService stationCommandService,
                             StationQueryService stationQueryService) {

        this.stationCommandService = stationCommandService;
        this.stationQueryService = stationQueryService;
    }

    @PostMapping("/stations")
    public ResponseEntity<Void> createStation(@RequestBody StationCreateRequestDto stationCreateRequest) {
        logger.info("[StationController.createStation] stationCreateRequest={}", stationCreateRequest);

        Station savedStation = stationCommandService.create(stationCreateRequest.getName());

        return ResponseEntity.created(URI.create("/stations/" + savedStation.getId())).build();
    }

    @GetMapping("/stations")
    public ResponseEntity<List<Station>> getStations() {
        List<Station> stations = stationQueryService.getStations();

        return ResponseEntity.ok().body(stations);
    }

    @GetMapping("/stations/{id}")
    public ResponseEntity<StationResponseDto> getStation(@PathVariable Long id) {
        logger.info("[StationController.getStation] id={}", id);

        Station savedStation = stationQueryService.getStation(id);

        return ResponseEntity.ok().body(StationResponseDto.of(savedStation.getName()));
    }

}
