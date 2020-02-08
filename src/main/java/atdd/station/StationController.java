package atdd.station;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class StationController {

    @PostMapping("/station")
    public ResponseEntity createStation(@RequestBody StationDto stationDto) {
        Station station = Station.of(stationDto);

        return ResponseEntity.created(URI.create("station/1"))
                .body(station);

    }

}
