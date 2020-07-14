package nextstep.subway.map.ui;

import nextstep.subway.map.dto.MapResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MapController {

    @GetMapping(value = "/maps", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MapResponse> getLineMap() {

        return ResponseEntity.ok(null);
    }
}
