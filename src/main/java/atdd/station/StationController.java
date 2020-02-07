package atdd.station;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("stations")
public class StationController {

    @PostMapping
    public ResponseEntity crate() {
        return ResponseEntity.created(URI.create("stations/1")).build();
    }

}
