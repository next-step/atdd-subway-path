package atdd.station;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class StationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StationController.class);

    @PostMapping(value = "/stations")
    public ResponseEntity createStation() {
        return ResponseEntity.created(URI.create("/station/1")).build();
    }

}
