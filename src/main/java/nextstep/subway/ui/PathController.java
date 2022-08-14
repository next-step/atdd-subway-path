package nextstep.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/paths")
public class PathController {
	private final PathService pathService;

	@GetMapping
	public ResponseEntity<PathResponse> getPath(@RequestParam Long source, @RequestParam Long target) {
		PathResponse pathResponse = pathService.findPath(source, target);
		return ResponseEntity.ok().body(pathResponse);
	}
}
