package atdd.path.web;

import atdd.path.domain.Station;
import atdd.path.dto.StationRequestView;
import atdd.path.dto.StationResponseView;
import atdd.path.serivce.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static atdd.path.PathConstant.BASE_URI_STATION;

@RestController
@RequestMapping(value = BASE_URI_STATION)
public class StationController {
    private StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity createStation(@RequestBody StationRequestView requestView) {
        Station savedStation = stationService.create(Station.of(requestView));
        return ResponseEntity
                .created(URI.create(BASE_URI_STATION + savedStation.getId()))
                .body(StationResponseView.of(savedStation));
    }

    @GetMapping("/{id}")
    public ResponseEntity retrieveStation(@PathVariable Long id) {
        Station station = StationService.findById(id);
        return ResponseEntity
                .ok()
                .body(StationResponseView.of(station));
    }

    @GetMapping
    public ResponseEntity showAllStations() {
        List<Station> stations = StationService.findAll();
        List<StationResponseView> responseViews
                = stations.stream()
                .map(it -> StationResponseView.of(it))
                .collect(Collectors.toList());
        return ResponseEntity
                .ok()
                .body(responseViews);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationService.deleteById();
        return ResponseEntity
                .noContent()
                .build();
    }
}