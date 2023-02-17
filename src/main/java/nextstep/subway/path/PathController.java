package nextstep.subway.path;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class PathController {

	private final PathService pathService;

	@GetMapping("/paths")
	public ResponseEntity<PathResponse> getPathSearch(@RequestParam Long source, @RequestParam Long target) {
		return ResponseEntity.ok().body(pathService.pathSearch(source, target));
	}
}
