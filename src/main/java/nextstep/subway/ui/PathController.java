package nextstep.subway.ui;

import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> getPaths(@RequestParam Long source, @RequestParam Long target) {

        return ResponseEntity.ok().body(new PathResponse(null, 0));
    }

}
