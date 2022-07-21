package nextstep.subway.station.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/ex/runtime")
    public ResponseEntity test() {
        throw new RuntimeException("asdf");
    }

    @GetMapping("/ex/illegal")
    public ResponseEntity test2() {
        throw new IllegalArgumentException("asdf");
    }
}
