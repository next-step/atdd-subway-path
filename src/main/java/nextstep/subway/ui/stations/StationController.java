package nextstep.subway.ui.stations;

import nextstep.subway.applicaion.station.StationCUDDoer;
import nextstep.subway.applicaion.station.StationFinder;
import nextstep.subway.applicaion.station.StationService;
import nextstep.subway.applicaion.dto.station.StationRequest;
import nextstep.subway.applicaion.dto.station.StationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {
    private StationCUDDoer stationCUDDoer;
    private StationFinder stationFinder;

    public StationController(StationCUDDoer stationCUDDoer, StationFinder stationFinder) {
        this.stationCUDDoer = stationCUDDoer;
        this.stationFinder = stationFinder;
    }

    @PostMapping()
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        StationResponse station = stationCUDDoer.saveStation(stationRequest);
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(station);
    }

    @GetMapping()
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(stationFinder.findAllStations());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationCUDDoer.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }
}
