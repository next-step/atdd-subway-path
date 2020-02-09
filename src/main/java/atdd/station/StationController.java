package atdd.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class StationController {

    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @PostMapping("/stations")
    public ResponseEntity createStation(@RequestBody Station station){
        Station save = stationRepository.save(station);
        return ResponseEntity
                .created(URI.create("/stations/"+save.getId()))
                .body(save);
    }

    @GetMapping("/stations")
    public ResponseEntity getStations(){
        List<Station> stations = stationRepository.findAll();
        return ResponseEntity
                .ok()
                .body(stations);

    }
}
