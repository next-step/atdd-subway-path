package atdd.station.controller;

import atdd.global.exception.ServiceNotFoundException;
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
import java.util.Map;

import static java.util.stream.Collectors.toList;

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
                .created(URI.create(request.getServletPath() + "/" + persistStation.getId()))
                .body(new StationResponseView(persistStation));
    }

    @GetMapping
    public ResponseEntity<StationListResponseView> getStations() {
        final List<Station> stations = stationRepository.findAll();
        final List<StationResponseView> views = stations.stream().map(StationResponseView::new).collect(toList());

        return ResponseEntity.ok(new StationListResponseView(stations.size(), views));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationResponseView> getStation(@PathVariable("id") Long id) {
        final Station station = getStationById(id);
        return ResponseEntity.ok(new StationResponseView(station));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteStation(@PathVariable("id") Long id) {
        final Station station = getStationById(id);
        stationRepository.deleteById(station.getId());

        return ResponseEntity.noContent().build();
    }

    private Station getStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException("지하철 역이 존재하지 않습니다.", Map.of("id", id)));
    }

}
