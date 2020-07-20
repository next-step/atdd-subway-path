package nextstep.subway.path.ui;

import nextstep.subway.path.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PathController {

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> searchShortestPath(@RequestParam("source") Integer sourceStationId,
                                                           @RequestParam("target") Integer targetStationIs) {
        return ResponseEntity.ok().build();
    }

}
