package atdd.station;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class StationController {

    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @PostMapping("/station")
    public ResponseEntity createStation(@RequestBody StationDto stationDto) {
        Station station = Station.of(stationDto);
        Station savedStation = stationRepository.save(station);

        return ResponseEntity.created(URI.create("station/" + savedStation.getId()))
                .body(StationDto.responseBuilder()
                        .station(savedStation)
                        .build());
    }

    @GetMapping("/stations")
    public ResponseEntity findAllStations() {
        return ResponseEntity.ok(stationRepository.findAll());
    }

}
