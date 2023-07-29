package nextstep.subway.web;

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

import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionAddRequest;

@RequestMapping("/lines")
@RestController
public class LineController {

	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	public ResponseEntity<LineResponse> createLine(@RequestBody LineCreateRequest lineCreateRequest) {
		LineResponse lineResponse = lineService.save(lineCreateRequest);
		return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId()))
			.body(lineResponse);
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> showLines() {
		return ResponseEntity.ok().body(LineResponse.from(lineService.findAll()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
		return ResponseEntity.ok()
			.body(lineService.findById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updateLine(@PathVariable Long id,
		@RequestBody LineUpdateRequest lineUpdateRequest) {
		lineService.update(id, lineUpdateRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
		lineService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/sections")
	public ResponseEntity<LineResponse> createSection(@PathVariable Long id,
		@RequestBody SectionAddRequest sectionAddRequest) {
		LineResponse lineResponse = lineService.addSection(id, sectionAddRequest);
		return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId()))
			.body(lineResponse);
	}

	@DeleteMapping("/{id}/sections")
	public ResponseEntity<Void> deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
		lineService.deleteSection(id, stationId);
		return ResponseEntity.noContent().build();
	}
}
