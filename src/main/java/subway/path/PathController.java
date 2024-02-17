package subway.path;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import subway.dto.path.PathResponse;

@RequestMapping("/paths")
@RestController
public class PathController {
	@GetMapping
	public ResponseEntity<PathResponse> paths() {
		return ResponseEntity.ok().body(null);
	}
}
