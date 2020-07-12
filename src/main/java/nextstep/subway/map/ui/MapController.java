package nextstep.subway.map.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.map.dto.MapResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/maps")
public class MapController {

    private final LineService lineService;

    public MapController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping
    public ResponseEntity<MapResponse> getLineMap() {

        return null;
    }
}
