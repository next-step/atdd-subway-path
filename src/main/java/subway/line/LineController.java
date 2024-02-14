package subway.line;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import subway.dto.line.LineCreateRequest;
import subway.dto.line.LineResponse;
import subway.dto.line.LineUpdateRequest;
import subway.dto.section.SectionRequest;
import subway.station.Station;
import subway.station.StationService;

@RequestMapping("/lines")
@RestController
public class LineController {
	private final LineService lineService;
	private final StationService stationService;

	public LineController(
		LineService lineService,
		StationService stationService
	) {
		this.lineService = lineService;
		this.stationService = stationService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> line(@PathVariable Long id) {
		LineResponse lineResponse = lineService.findLineById(id);
		return ResponseEntity.ok(lineResponse);
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> lines() {
		List<LineResponse> responses = lineService.lines();
		return ResponseEntity.ok(responses);
	}

	@PostMapping
	public ResponseEntity<LineResponse> saveLine(@RequestBody LineCreateRequest request) {
		Station upStation = stationService.findStationById(request.getUpStationId());
		Station downStation = stationService.findStationById(request.getDownStationId());

		LineResponse lineResponse = lineService.save(request.toEntity(), upStation, downStation, request.getDistance());

		return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineUpdateRequest request) {
		lineService.update(id, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
		lineService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/sections")
	public ResponseEntity<LineResponse> addSections(@PathVariable Long id, @RequestBody SectionRequest request) {
		Station upStation = stationService.findStationById(request.getUpStationId());
		Station downStation = stationService.findStationById(request.getDownStationId());

		LineResponse lineResponse = lineService.addSection(id, upStation, downStation, request.getDistance());

		return ResponseEntity.created(URI.create("/lines/" + id)).body(lineResponse);
	}

	@DeleteMapping("/{id}/sections")
	public ResponseEntity<Void> deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
		Station deleteTargetStation = stationService.findStationById(stationId);

		lineService.deleteSection(id, deleteTargetStation);

		return ResponseEntity.noContent().build();
	}
}
