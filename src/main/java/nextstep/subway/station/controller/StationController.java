package nextstep.subway.station.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
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
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        StationResponse response = stationService.saveStation(stationRequest);
        return ResponseEntity.created(URI.create("/stations/" + response.getId())).body(response);
    }

    @GetMapping(value = "/stations")
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> responses = stationService.findAllStations();
        return ResponseEntity.ok().body(responses);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }
}
