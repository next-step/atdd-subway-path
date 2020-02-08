package atdd.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@RestController
public class StationController {

    @PostMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createStation(@RequestBody Map<String, String> map, HttpServletRequest request) throws URISyntaxException {
        String stationName = map.get("name");

        return ResponseEntity.created(new URI(request.getRequestURI()))
                .body("{\"name\":\"" + stationName + "\"}");
    }
}
