package atdd.line.controller;

import atdd.line.api.request.CreateLineRequestView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@Slf4j
@RequestMapping("/lines")
@RestController
public class LineController {

    @PostMapping
    public ResponseEntity createLine(@RequestBody CreateLineRequestView view, HttpServletRequest request) {
        log.debug("[dev] view: {}", view);
        return ResponseEntity
                .created(URI.create(request.getServletPath() +"/1"))
                .build();
    }

}
