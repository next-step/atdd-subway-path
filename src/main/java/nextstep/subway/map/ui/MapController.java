package nextstep.subway.map.ui;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.map.application.MapService;
import nextstep.subway.map.dto.MapResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/maps")
public class MapController {

    private MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping
    public ResponseEntity<MapResponse> findAllMaps() {
        return ResponseEntity.ok(mapService.findAllMaps());
    }
}
