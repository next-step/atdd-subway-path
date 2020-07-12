package nextstep.subway.path.ui;

import com.google.common.collect.Lists;
import nextstep.subway.path.dto.PathRequest;
import nextstep.subway.path.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {

    @GetMapping
    public ResponseEntity<PathResponse> searchPath(PathRequest pathRequest) {
        return ResponseEntity.ok(new PathResponse(Lists.newArrayList(), 10, 10));
    }
}
