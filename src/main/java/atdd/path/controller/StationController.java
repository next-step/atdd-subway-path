package atdd.path.controller;

import atdd.path.domain.Station;
import atdd.path.domain.dto.StationDto;
import atdd.path.repository.StationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("stations")
public class StationController {

    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @PostMapping("")
    public ResponseEntity createStation(@RequestBody StationDto stationDto) {
        Station savedStation = stationRepository.save(stationDto.toStation());

        return ResponseEntity.created(URI.create("station/" + savedStation.getId()))
                .body(StationDto.of(savedStation));
    }

    @GetMapping("")
    public ResponseEntity findAllStations() {
        List<Station> stationList = stationRepository.findAll();

        return ResponseEntity.ok()
                .body(StationDto.listOf(stationList));
    }

    @GetMapping("/{id}")
    public ResponseEntity findStationById(@PathVariable Long id) {
        return stationRepository.findById(id)
                .map(it -> ResponseEntity.ok().body(StationDto.of(it)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
