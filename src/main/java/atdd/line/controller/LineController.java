package atdd.line.controller;

import atdd.line.api.request.CreateLineRequestView;
import atdd.line.api.response.LineResponseView;
import atdd.line.domain.Line;
import atdd.line.service.LineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/lines")
@RestController
public class LineController {

    private final LineService lineService;

    @PostMapping
    public ResponseEntity<LineResponseView> createLine(@RequestBody CreateLineRequestView view, HttpServletRequest request) {
        final Line line = view.toLine();
        final Line persistLine = lineService.save(line);

        return ResponseEntity
                .created(URI.create(request.getServletPath() + "/" + persistLine.getId()))
                .body(new LineResponseView(persistLine));
    }

}
