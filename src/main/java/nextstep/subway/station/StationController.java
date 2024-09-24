package nextstep.subway.station;

import lombok.RequiredArgsConstructor;
import nextstep.subway.path.api.PathService;
import nextstep.subway.path.domain.PathEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class StationController {
    private final StationService stationService;
    private final PathService pathService;

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        StationResponse station = stationService.saveStation(stationRequest, new PathEvent(this));
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(station);
    }

    @GetMapping(value = "/stations")
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(stationService.findAllStations());
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStationById(id, new PathEvent(this));
        return ResponseEntity.noContent().build();
    }
}
