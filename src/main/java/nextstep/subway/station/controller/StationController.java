package nextstep.subway.station.controller;

import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.service.StationService;

import java.net.URI;
import java.util.List;

@RestController
public class StationController {
    private StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(final @RequestBody StationRequest stationRequest) {
        final StationResponse station = stationService.saveStation(stationRequest);

        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(station);
    }

    @GetMapping(value = "/stations")
    public ResponseEntity<List<StationResponse>> showStations() {
        final List<StationResponse> stations = stationService.findAllStations();

        return ResponseEntity.ok().body(stations);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(final @PathVariable Long id) {
        stationService.deleteStationById(id);

        return ResponseEntity.noContent().build();
    }
}
