package nextstep.subway.station.controller;

import lombok.RequiredArgsConstructor;
import nextstep.subway.station.dto.request.SaveStationRequest;
import nextstep.subway.station.dto.response.StationResponse;
import nextstep.subway.station.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class StationController {
    private final StationService stationService;

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody @Valid SaveStationRequest stationRequest) {
        StationResponse station = stationService.saveStation(stationRequest);
        return ResponseEntity
                .created(URI.create(String.format("/stations/%d", station.getId())))
                .body(station);
    }

    @GetMapping(value = "/stations")
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok(stationService.findAllStations());
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
