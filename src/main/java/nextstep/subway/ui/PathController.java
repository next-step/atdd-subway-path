package nextstep.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;

@RequestMapping("/paths")
@RestController
@RequiredArgsConstructor
public class PathController {

	private final PathService pathService;

	@GetMapping
	public ResponseEntity<PathResponse> getPath(
		@RequestParam(value = "source", required = false) Long source,
		@RequestParam(value = "target", required = false) Long target) {
		return ResponseEntity.ok(pathService.getPath(source, target));
	}

}
