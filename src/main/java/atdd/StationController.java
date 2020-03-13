package atdd;

import atdd.domain.Station;
import atdd.dto.StationRequestView;
import atdd.dto.StationResponseView;
import atdd.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/stations")
public class StationController {
    private StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody StationRequestView requestView){
        Station station = stationService.create(Station.of(requestView));
        return ResponseEntity
                .created(URI.create("/stations/"+station.getId()))
                .body(StationResponseView.of(station));
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable Long id){
        Station station = stationService.findById(id);
        StationResponseView responseView = StationResponseView.of(station);
        return ResponseEntity
                .ok()
                .body(responseView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id){
        stationService.delete(id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
