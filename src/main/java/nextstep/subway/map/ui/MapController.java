package nextstep.subway.map.ui;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.map.dto.MapResponse;

@RestController
public class MapController {
    private LineService lineService;

    public MapController(LineService lineService) {
        this.lineService = lineService;
    }    

    @GetMapping("/maps")
    public ResponseEntity<MapResponse> getMaps() {
        List<LineResponse> response = lineService.findAllLinesForMaps(); 
        return ResponseEntity.ok(new MapResponse(response));       
    }    
}
