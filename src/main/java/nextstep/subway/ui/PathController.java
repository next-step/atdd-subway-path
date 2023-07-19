package nextstep.subway.ui;

import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class PathController {
    @GetMapping("/paths")
    public ResponseEntity<PathRequest> deleteStation(@RequestParam Long source, @RequestParam Long target) {
        return ResponseEntity.ok().body(new PathRequest());
    }
}
