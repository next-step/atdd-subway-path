package atdd.station;

import atdd.station.api.request.CreateStationRequest;
import atdd.station.api.response.FindStationResponse;
import atdd.station.api.response.StationResponse;
import atdd.station.repository.Station;
import atdd.station.repository.StationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class StationController {

    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody CreateStationRequest request) {
        final Station station = request.toStation();
        final Station persistStation = stationRepository.save(station);

        return ResponseEntity
                .created(URI.create("/stations/" + persistStation.getId()))
                .body(new StationResponse(persistStation));
    }

    @GetMapping("/stations")
    public ResponseEntity<FindStationResponse> findStations() {
        final List<Station> stations = stationRepository.findAll();
        return ResponseEntity.ok(new FindStationResponse(stations.size(), stations));
    }

    @GetMapping("/stations/{id}")
    public ResponseEntity<StationResponse> findStationById(@PathVariable("id") Long id) {
        return stationRepository.findById(id)
                .map(station -> ResponseEntity.ok(new StationResponse(station)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Object> deleteStation(@PathVariable("id") Long id) {
        return stationRepository.findById(id)
                .map(
                    station -> {
                        stationRepository.deleteById(station.getId());
                        return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

}
