package nextstep.subway.ui;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {

    @Autowired
    PathService pathService;

    @GetMapping()
    public ResponseEntity<PathResponse> showPaths(@RequestParam Long source, @RequestParam Long target) {
        PathResponse response = pathService.showPaths(source, target);
        return ResponseEntity.ok().body(response);
    }
}
