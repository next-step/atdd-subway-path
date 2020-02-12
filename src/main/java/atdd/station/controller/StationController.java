package atdd.station.controller;

import atdd.station.model.CreateStationRequestView;
import atdd.station.model.Station;
import atdd.station.repository.StationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {
    private static final Logger logger = LoggerFactory.getLogger(StationController.class);

    @Autowired
    private StationRepository stationRepository;

    @PostMapping
    @ResponseBody
    public ResponseEntity<Station> createStation(@RequestBody CreateStationRequestView view) {
        Station station = stationRepository.save(view.toStation());

        return ResponseEntity.created(URI.create("/stations/" + station.getId()))
                .body(station);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Station>> findAllStations() {

        List stations = stationRepository.findAll();

        return ResponseEntity.ok()
                .body(stations);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Station> findStation(@PathVariable long id) {
        return ResponseEntity
                .ok()
                .body(stationRepository.findById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity deleteStation(@PathVariable long id) {
        stationRepository.deleteById(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
