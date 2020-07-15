package nextstep.subway.map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/maps")
public class MapController {

	private final MapService mapService;

	public MapController(MapService mapService) {
		this.mapService = mapService;
	}

	public ResponseEntity<MapResponseDto> responseMap() {
		MapResponseDto response = mapService.responseMap();
		return ResponseEntity.ok().body(response);
	}
}
