package nextstep.subway.ui;

import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class PathController {

    @GetMapping(
        value = "/paths",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PathResponse> getPath(@RequestParam String source, @RequestParam String target) {
        return ResponseEntity.ok().build();
    }
}
