package nextstep.subway.ui;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.dto.PathResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {
    @Autowired
    private LineService lineService;
    @GetMapping("/paths")
    public ResponseEntity<PathResponse> path(@PathVariable Long source, Long target) {
        lineService.getPath(source, target);
        return null;
    }
}
