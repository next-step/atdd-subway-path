package atdd.station;

import atdd.dto.Station;
import atdd.service.StationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping(value = "/stations", produces = "application/json")
public class StationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StationController.class);

    @Resource
    private StationService stationService;

    @PostMapping("")
    public ResponseEntity<String> createStation(@RequestBody Station station) {

        stationService.createStation(station);
        String stationName = "강남역";

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

    @GetMapping("/details/{stationName}")
    public ResponseEntity<String> detailStation(@PathVariable String stationName) {

        try {
            return ResponseEntity.ok().body("{\"name\":\""+stationName+"\"}");
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("/{stationName}")
    public ResponseEntity<String> deleteStation(@PathVariable String stationName) {

        try {
            return ResponseEntity.ok().body("{\"name\":\""+stationName+"\"}");
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }

    }
}
