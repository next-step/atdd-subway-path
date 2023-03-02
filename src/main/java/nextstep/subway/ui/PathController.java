package nextstep.subway.ui;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;

@RestController
@RequestMapping("/paths")
public class PathController {
	private final PathService pathService;

	public PathController(PathService pathService) {
		this.pathService = pathService;
	}

	@PostMapping
	public ResponseEntity<PathResponse> createLine(@Valid @RequestBody PathRequest pathRequest) {
		PathResponse path = pathService.getPath(pathRequest);
		return ResponseEntity.ok().body(path);
	}
}
