package atdd.station.controller;

import atdd.station.api.request.CreateStationRequestView;
import atdd.station.api.response.StationListResponseView;
import atdd.station.api.response.StationResponseView;
import atdd.station.domain.Station;
import atdd.station.domain.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/stations")
@RestController
public class StationController {

    private final StationRepository stationRepository;

    @PostMapping
    public ResponseEntity<StationResponseView> createStation(@RequestBody CreateStationRequestView view, HttpServletRequest request) {
        final Station station = view.toStation();
        final Station persistStation = stationRepository.save(station);

        return ResponseEntity
                .created(URI.create(request.getServletPath() +"/"+ persistStation.getId()))
                .body(new StationResponseView(persistStation));
    }

    @GetMapping
    public ResponseEntity<StationListResponseView> getStations() {
        final List<Station> stations = stationRepository.findAll();
        return ResponseEntity.ok(new StationListResponseView(stations.size(), stations));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationResponseView> getStationById(@PathVariable("id") Long id) {
        return stationRepository.findById(id)
                .map(station -> ResponseEntity.ok(new StationResponseView(station)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteStation(@PathVariable("id") Long id) {
        return stationRepository.findById(id)
                .map(
                    station -> {
                        stationRepository.deleteById(station.getId());
                        return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

}
