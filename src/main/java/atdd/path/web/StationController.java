package atdd.path.web;

import atdd.path.application.dto.StationRequestView;
import atdd.path.application.dto.StationResponseView;
import atdd.path.domain.Station;
import atdd.path.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/stations")
public class StationController {
    private StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody StationRequestView requestView) {
        StationResponseView responseView = stationService.create(requestView);
        return ResponseEntity
                .created(URI.create("/stations/" + responseView.getId()))
                .body(responseView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) throws Exception {
        stationService.delete(StationRequestView.builder()
                                                 .id(id)
                                                 .build());
        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable Long id) throws Exception{
        Station station = stationService.findById(StationRequestView.builder()
                                                                 .id(id)
                                                                 .build());
        return ResponseEntity
                .ok()
                .body(station);
    }
}
