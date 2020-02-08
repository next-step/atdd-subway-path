package atdd.station;

import atdd.repository.StationRepository;
import atdd.vo.CreateStationRequestView;
import atdd.vo.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class StationController {
    private static final Logger logger = LoggerFactory.getLogger(StationController.class);
    @Autowired
    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @PostMapping(value = "/stations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createStation(
            @RequestBody CreateStationRequestView view) {
        Station station = view.toStation();
        Station persistStation = stationRepository.save(station);
        logger.info(station.toString());

        return ResponseEntity
                .created(URI.create("/stations/" + persistStation.getId()))
                .body(persistStation.toResponseView());
    }
}
