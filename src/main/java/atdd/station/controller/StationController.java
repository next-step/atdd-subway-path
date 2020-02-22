package atdd.station.controller;

import atdd.station.model.CreateStationRequestView;
import atdd.station.model.entity.Station;
import atdd.station.repository.StationRepository;
import atdd.station.service.StationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stations")
public class StationController {
    private static final Logger logger = LoggerFactory.getLogger(StationController.class);

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private StationService stationService;

    @PostMapping
    public ResponseEntity<Station> createStation(@RequestBody CreateStationRequestView view) {
        final Station station = stationRepository.save(view.toStation());

        return ResponseEntity.created(URI.create("/stations/" + station.getId()))
                .body(station);
    }

    @GetMapping
    public ResponseEntity<List<Station>> findAllStations() {
        final List<Station> stations = stationRepository.findAll();

        stations.forEach(data -> data.setStationLineDtos(stationService.lineDtos(data.getLineIds())));

        return ResponseEntity.ok(stations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Station> findStation(@PathVariable long id) {
        final Optional<Station> optionalStation = stationRepository.findById(id);

        if(optionalStation.isPresent()) {
            Station station = optionalStation.get();
            station.setStationLineDtos(stationService.lineDtos(station.getLineIds()));

            return ResponseEntity.ok(station);
        }

        return ResponseEntity
                .noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable long id) {
        stationRepository.deleteById(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
