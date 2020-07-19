package nextstep.subway.map.ui;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MapController {
    @GetMapping("/maps")
    public ResponseEntity getMaps() {
        return new ResponseEntity<>(HttpStatus.OK);
    }    
}
