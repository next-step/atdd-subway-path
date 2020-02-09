package atdd.station;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("stations")
public class StationController {

    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @PostMapping("")
    public ResponseEntity createStation(@RequestBody StationDto stationDto) {
        Station station = Station.of(stationDto);
        Station savedStation = stationRepository.save(station);

        return ResponseEntity.created(URI.create("station/" + savedStation.getId()))
                .body(StationDto.responseBuilder()
                        .station(savedStation)
                        .build());
    }

    @GetMapping("")
    public ResponseEntity findAllStations() {
        return ResponseEntity.ok(stationRepository.findAll());
    }

    @GetMapping("/{name}")
    public ResponseEntity findStationByName(@PathVariable String name) {
        return ResponseEntity.ok(stationRepository.findByName(name));
    }

    @DeleteMapping("")
    public ResponseEntity deleteStation(@RequestParam String name) {
        Station targetStation = stationRepository.findByName(name);
        Assert.notNull(targetStation, "Not found delete target name station. station name: " + name);

        stationRepository.delete(targetStation);

        return ResponseEntity.ok().build();
    }
}
