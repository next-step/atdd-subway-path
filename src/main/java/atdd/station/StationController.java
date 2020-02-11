package atdd.station;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/stations")
public class StationController {

    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @PostMapping("")
    public ResponseEntity createStation(@RequestBody StationCreateRequest station) {
        Station save = stationRepository.save(station.toEntity());

        return ResponseEntity.created(URI.create("/stations/" + save.getId())).body(StationResponse.of(save));
    }

    @GetMapping("")
    public ResponseEntity getStations() {
        List<Station> stations = stationRepository.findAll();
        return ResponseEntity.ok().body(StationListResponse.of(stations));

    }

    @GetMapping("/{id}")
    public ResponseEntity getStationById(@PathVariable Long id) {
        Station station = stationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 역입니다 {" + id + "}"));
        return ResponseEntity.ok().body(StationResponse.of(station));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStationById(@PathVariable Long id) {
        stationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
