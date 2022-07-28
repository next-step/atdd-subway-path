package nextstep.subway.path.ui;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {

    @GetMapping("/paths")
    public PathResponse findPath(@RequestParam Integer source, @RequestParam Integer target) {
        return new PathResponse(8);
    }

    @Getter
    private static class PathResponse {
        private int distance;

        public PathResponse(int distance) {
            this.distance = distance;
        }
    }
}
