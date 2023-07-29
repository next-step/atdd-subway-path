package nextstep.subway.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.application.PathService;
import nextstep.subway.dto.PathRequest;
import nextstep.subway.dto.PathResponse;

@RequestMapping("/paths")
@RestController
public class PathController {

	private final PathService pathService;

	public PathController(PathService pathService) {
		this.pathService = pathService;
	}

	@GetMapping
	public ResponseEntity<PathResponse> showPath(@ModelAttribute PathRequest pathRequest) {
		return ResponseEntity.ok().body(pathService.findPath(pathRequest));
	}
}
