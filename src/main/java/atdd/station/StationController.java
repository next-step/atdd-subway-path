package atdd.station;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/stations")
public class StationController {

    @PostMapping("")
    public ResponseEntity<String> create() {

        return ResponseEntity.created(URI.create("/stations")).build();
    }
}
