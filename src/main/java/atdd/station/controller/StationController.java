package atdd.station.controller;

import atdd.station.model.Station;
import atdd.station.model.StationRequest;
import atdd.station.model.StationResponse;
import atdd.station.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/stations")
public class StationController {

    private final StationService stationService;

    public StationController(final StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping(value = "")
    public ResponseEntity<StationResponse> createStation(@RequestBody final StationRequest stationRequest) {
        final Station station = stationService.save(stationRequest);

        return ResponseEntity.created(URI.create("/stations/"))
                .body(StationResponse.of(station));
    }

    @GetMapping(value = "")
    public ResponseEntity<List<StationResponse>> findAllStation() {
        final List<Station> stations = stationService.findAll();
        return ResponseEntity.ok(StationResponse.listOf(stations));
    }

    @GetMapping(value = "/{name}")
    public ResponseEntity<StationResponse> findStationByName(@PathVariable final String name) {
        final Station station = stationService.findByName(name);
        System.out.println(station.getId() + " - " +station.getName());
        return ResponseEntity.ok(StationResponse.of(station));
    }

    @DeleteMapping("")
    public ResponseEntity deleteStation(@RequestParam("name") final String name) {
        stationService.deleteStation(name);
        return ResponseEntity.ok().build();
    }
}
