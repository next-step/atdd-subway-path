package nextstep.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/paths")
public class PathController {
    private final PathService pathService;

    @GetMapping
    public ResponseEntity<PathResponse> getPath(@RequestParam(value = "source") Long source, @RequestParam(value = "target") Long target ) {
        PathResponse pathResponse = pathService.searchPath(source, target);
        return ResponseEntity.ok(pathResponse);
    }
}
