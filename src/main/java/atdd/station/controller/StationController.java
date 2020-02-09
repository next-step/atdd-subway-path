package atdd.station.controller;

import atdd.station.domain.Station;
import atdd.station.domain.dto.StationDto;
import atdd.station.repository.StationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;

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
        Station station = stationRepository.findById(id).orElse(null);

        if (Objects.isNull(station)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .body(StationDto.of(station));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
