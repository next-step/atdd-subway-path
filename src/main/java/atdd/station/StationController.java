package atdd.station;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping(value = "/stations", produces = "application/json")
public class StationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StationController.class);

    @PostMapping("")
    public ResponseEntity<String> createStation(@RequestBody Map<String, String> param) {

        String stationName = param.get("name");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Location", stationName);

        return ResponseEntity.created(URI.create("/stations/" + stationName)).headers(httpHeaders).body("{\"name\":\""+stationName+"\"}");
    }

    @GetMapping("/{stationName}")
    public ResponseEntity<String> getStation(@PathVariable String stationName) {

        try {
            return ResponseEntity.ok().body("{\"name\":\""+stationName+"\"}");
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }

    }

}
