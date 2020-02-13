package atdd.line.controller;

import atdd.line.domain.Line;
import atdd.line.dto.CreateLineAndStationsRequest;
import atdd.line.dto.CreateLineRequest;
import atdd.line.dto.FindLineResponse;
import atdd.line.service.LineService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {

	private LineService lineService;

	public LineController(final LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping("/lines")
	public ResponseEntity<Line> createLine(@RequestBody CreateLineRequest request) {
		Line line = lineService.save(request);
		return ResponseEntity.created(URI.create("/lines" + line.getId()))
			.contentType(MediaType.APPLICATION_JSON)
			.body(line);
	}

	@PostMapping("/line-stations")
	public ResponseEntity<Line> createLineAndStations(@RequestBody CreateLineAndStationsRequest request) {
		Line line = lineService.saveLineAndStations(request);
		return ResponseEntity.ok(line);
	}

	@GetMapping("/lines")
	public ResponseEntity<List<Line>> findAll() {
		return ResponseEntity.ok(lineService.findAll());
	}

	@GetMapping("/line")
	public ResponseEntity<FindLineResponse> findByName(@RequestParam("name") String name) {
		return ResponseEntity.ok(lineService.findByName(name));
	}
}
