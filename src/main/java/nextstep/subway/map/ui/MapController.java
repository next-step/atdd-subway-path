package nextstep.subway.map.ui;

import nextstep.subway.map.dto.MapResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MapController {

    @GetMapping("/maps")
    public ResponseEntity<MapResponse> loadMap() {
        return ResponseEntity.ok(new MapResponse());
    }
}
