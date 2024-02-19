package subway.path;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import subway.dto.path.PathResponse;
import subway.station.Station;
import subway.station.StationService;

@RequestMapping("/paths")
@RestController
public class PathController {
	private final PathService pathService;
	private final StationService stationService;

	public PathController(PathService pathService, StationService stationService) {
		this.pathService = pathService;
		this.stationService = stationService;
	}

	@GetMapping
	public ResponseEntity<PathResponse> paths(@RequestParam Long source, @RequestParam Long target) {
		Station sourceStation = stationService.findStationById(source);
		Station targetStation = stationService.findStationById(target);
		PathResponse response = pathService.findShortestPath(sourceStation, targetStation);
		return ResponseEntity.ok().body(response);
	}
}
