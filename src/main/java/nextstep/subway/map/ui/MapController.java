package nextstep.subway.map.ui;

import nextstep.subway.map.application.MapService;
import nextstep.subway.map.dto.MapResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MapController {

    private final MapService MapService;

    public MapController(MapService MapService) {
        this.MapService = MapService;
    }

    @GetMapping(value = "/maps", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MapResponse> getLineMap() {
        MapResponse mapResponse = MapService.findAllLineAndStation();
        return ResponseEntity.ok().body(mapResponse);
    }
}
