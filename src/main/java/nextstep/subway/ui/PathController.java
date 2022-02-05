package nextstep.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {

    @GetMapping("/paths")
    public ResponseEntity<?> getShortestPath() {
        return ResponseEntity.badRequest().build();
    }
}
