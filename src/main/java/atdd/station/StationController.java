package atdd.station;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/station")
public class StationController {

    @PostMapping
    public ResponseEntity<String> create() {
        return ResponseEntity.created(URI.create("/station/1")).build();
    }

}
