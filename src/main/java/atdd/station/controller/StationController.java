package atdd.station.controller;

import atdd.station.api.request.CreateStationRequest;
import atdd.station.api.response.FindStationsResponse;
import atdd.station.api.response.StationResponse;
import atdd.station.domain.Station;
import atdd.station.domain.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/stations")
@RestController
public class StationController {

    private final StationRepository stationRepository;

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody CreateStationRequest request) {
        final Station station = request.toStation();
        final Station persistStation = stationRepository.save(station);

        return ResponseEntity
                .created(URI.create("/stations/" + persistStation.getId()))
                .body(new StationResponse(persistStation));
    }

    @GetMapping
    public ResponseEntity<FindStationsResponse> findStations() {
        final List<Station> stations = stationRepository.findAll();
        return ResponseEntity.ok(new FindStationsResponse(stations.size(), stations));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationResponse> findStationById(@PathVariable("id") Long id) {
        return stationRepository.findById(id)
                .map(station -> ResponseEntity.ok(new StationResponse(station)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
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
