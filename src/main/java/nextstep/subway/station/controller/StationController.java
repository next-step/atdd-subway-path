package nextstep.subway.station.controller;

import nextstep.subway.path.service.PathFinder;
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
    private PathFinder pathFinder;

    public StationController(StationService stationService, PathFinder pathFinder) {
        this.stationService = stationService;
        this.pathFinder = pathFinder;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
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

    @GetMapping("/paths")
    public ResponseEntity<Object> retrieveStationPath(@RequestParam("source") Long source,
                                                      @RequestParam("target") Long target) {
        ;

        return ResponseEntity.ok().body(pathFinder.retrieveStationPath(source, target));
    }
}
