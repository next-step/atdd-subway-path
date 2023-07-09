package nextstep.subway.ui.station;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.station.StationService;
import nextstep.subway.applicaion.station.request.StationRequest;
import nextstep.subway.applicaion.station.response.StationResponse;

@RestController
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody final StationRequest request) {
        final var response = stationService.saveStation(request);
        return ResponseEntity
                .created(URI.create("/stations/" + response.getId()))
                .body(response);
    }

    @GetMapping(value = "/stations")
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity
                .ok()
                .body(stationService.findAllStations());
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable final Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
