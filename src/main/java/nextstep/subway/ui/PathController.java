package nextstep.subway.ui;


import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;

    @GetMapping
    public ResponseEntity<PathResponse> getPaths(
            @RequestParam Long source,
            @RequestParam Long target
    ) {
        PathResponse response = pathService.getPaths(source, target);
        return ResponseEntity.ok().body(response);
    }

}
