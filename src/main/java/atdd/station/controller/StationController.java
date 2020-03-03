package atdd.station.controller;

import atdd.station.api.request.CreateStationRequestView;
import atdd.station.api.response.StationResponseView;
import atdd.station.api.response.StationsResponseView;
import atdd.station.domain.Station;
import atdd.station.domain.query.StationQueryView;
import atdd.station.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/stations")
@RestController
public class StationController {

    public static final String STATION_URL = "/stations";

    private final StationService stationService;

    @PostMapping
    public ResponseEntity<StationResponseView> createStation(@RequestBody CreateStationRequestView view) {
        final Station station = view.toStation();
        final Station persistStation = stationService.save(station);

        return ResponseEntity
                .created(URI.create(STATION_URL + "/" + persistStation.getId()))
                .body(new StationResponseView(persistStation));
    }

    @GetMapping
    public ResponseEntity<StationsResponseView> getStations() {
        final List<Station> stations = stationService.findAll();
        return ResponseEntity.ok(new StationsResponseView(stations));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationQueryView> getStation(@PathVariable("id") Long id) {
        return ResponseEntity.ok(stationService.findStationWithLine(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteStation(@PathVariable("id") Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }

}
