package atdd.station.controller;

import static java.util.stream.Collectors.toList;

import atdd.station.domain.Station;
import atdd.station.dto.CreateStationRequest;
import atdd.station.dto.StationResponse;
import atdd.station.service.StationService;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> create(@Valid @RequestBody CreateStationRequest request) {
        Station station = stationService.create(request.toEntry());
        return ResponseEntity.created(URI.create("stations/" + station.getId()))
            .body(StationResponse.of(station));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationResponse> get(@PathVariable long id) {
        return ResponseEntity.ok(StationResponse.of(stationService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> getAll() {
        List<StationResponse> result = stationService.getAll().stream()
            .map(StationResponse::of)
            .collect(toList());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable long id) {
        return ResponseEntity.noContent().build();
    }

}
