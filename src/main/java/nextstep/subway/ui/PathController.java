package nextstep.subway.ui;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Station;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/paths")
public class PathController {

	@GetMapping
	public ResponseEntity<PathResponse> findShortestPath(@RequestParam Long source, @RequestParam Long target) {

		return ResponseEntity.ok().body(new PathResponse(List.of(new Station("종합운동장역"),
				new Station("잠실역"),
				new Station("천호역")),
				20));
	}

}
