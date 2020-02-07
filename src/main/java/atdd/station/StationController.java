package atdd.station;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(value = "/stations",
        produces = "application/json",
        method = {RequestMethod.GET, RequestMethod.GET})
public class StationController {

    @PostMapping("/create")
    public ResponseEntity create() {
        return ResponseEntity
                .created(URI.create("/station/1"))
                .body("{\"name\":\"강남역\"}");
    }

    @PostMapping("/list")
    public ResponseEntity list() {
        return ResponseEntity
                .ok()
                .body("{\"name\":\"강남역\"}");
    }
}
