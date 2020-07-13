package nextstep.subway.map.ui;

import nextstep.subway.map.dto.MapResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/maps")
public class MapController {

    @GetMapping
    public ResponseEntity<MapResponse> getMaps() {
        return ResponseEntity.ok().build();
    }
}
