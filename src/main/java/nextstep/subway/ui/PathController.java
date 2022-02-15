package nextstep.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;

@RestController
@RequestMapping("/paths")
public class PathController {
	PathService pathService;

	public PathController(PathService pathService) {
		this.pathService = pathService;
	}

	@GetMapping
	public ResponseEntity<PathResponse> getPaths(@RequestParam Long source, @RequestParam Long target) {
		PathResponse pathResponse = pathService.searchPath(source, target);
		return ResponseEntity.ok().body(pathResponse);
	}
}
