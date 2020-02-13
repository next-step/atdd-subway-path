package atdd.station;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/stations")
public class StationController {

    private final StationRepository stationRepository;
    private final StationService stationService;



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
        StationDetailResponse station = stationService.findByIdWithLineList(id);
        return ResponseEntity.ok().body(station);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStationById(@PathVariable Long id) {
        stationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
