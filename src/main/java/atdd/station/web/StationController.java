package atdd.station.web;

import atdd.station.application.StationCommandService;
import atdd.station.domain.Station;
import atdd.station.web.dto.StationCreateRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class StationController {
    private static final Logger logger = LoggerFactory.getLogger(StationController.class);

    private StationCommandService stationCommandService;

    public StationController(StationCommandService stationCommandService) {
        this.stationCommandService = stationCommandService;
    }

    @PostMapping("/stations")
    public ResponseEntity<Void> createStation(@RequestBody StationCreateRequestDto stationCreateRequest) {
        logger.info("[StationController.createStation] stationCreateRequest={}", stationCreateRequest);

        Station savedStation = stationCommandService.create(stationCreateRequest.getName());

        return ResponseEntity.created(URI.create("/stations/" + savedStation.getId())).build();
    }
}
