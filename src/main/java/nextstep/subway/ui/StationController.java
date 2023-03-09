package nextstep.subway.ui;

import java.net.URI;
import java.util.List;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class StationController {
    private StationService stationService;
    private final PathService pathService;

    public StationController(StationService stationService, PathService pathService) {
        this.stationService = stationService;
        this.pathService = pathService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(
            @RequestBody StationRequest stationRequest) {
        StationResponse station = stationService.saveStation(stationRequest);
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(station);
    }

    @GetMapping(value = "/stations")
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(stationService.findAllStations());
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stations/path")
    public ResponseEntity<PathResponse> searchPath(@RequestParam Long source, @RequestParam Long target) {
        return ResponseEntity.ok(pathService.searchPath(source, target));
    }
}
