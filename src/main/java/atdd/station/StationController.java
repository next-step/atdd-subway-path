package atdd.station;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StationController {
    @PostMapping("/stations")
    public ResponseEntity addStation(Map<String, Object> model){
        return ResponseEntity.ok("{\"name\":\"강남역\"}");
    }


}
