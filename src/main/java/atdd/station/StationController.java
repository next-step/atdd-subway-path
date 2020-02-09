package atdd.station;

import atdd.domain.Station;
import atdd.repository.StationRepository;
import atdd.vo.CreateStationRequestView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StationController {
    private static final Logger logger = LoggerFactory.getLogger(StationController.class);
    private StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @PostMapping("/stations")
    public ResponseEntity createStation(@RequestBody CreateStationRequestView view) {
        Station persistStation = stationRepository.save(view.toStation());
        return new ResponseEntity<>(persistStation, HttpStatus.CREATED);
    }

    @GetMapping("/stations")
    public ResponseEntity findStations() {
        List<Station> stationList = stationRepository.findAll();
        return new ResponseEntity<>(stationList, HttpStatus.OK);
    }

    @GetMapping("/station/{stationName}")
    public ResponseEntity findStations(@PathVariable("stationName") String stationName) {
        Station persistStation = stationRepository.findByStationName(stationName);
        return new ResponseEntity<>(persistStation, HttpStatus.OK);
    }
}
