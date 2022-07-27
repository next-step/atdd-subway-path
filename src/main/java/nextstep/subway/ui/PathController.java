package nextstep.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;

@RestController
public class PathController {

	private LineService lineService;
	private PathService pathService;

	public PathController(PathService pathService) {
		this.pathService = pathService;
	}

	@GetMapping("/paths")
	public ResponseEntity<Void> getPath(@RequestParam Long source, @RequestParam Long target) {

		pathService.getPath(new PathRequest(lineService.showLines(), source, target));
		return ResponseEntity.noContent().build();
	}
}
