package atdd.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("line")
public class LineController {

    private final LineRepository lineRepository;

    public LineController(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @PostMapping("")
    public ResponseEntity createLine(@RequestBody LineDto requestLine) {
        Line savedLine = lineRepository.save(requestLine.toLine());
        return ResponseEntity.created(URI.create("/line/" + savedLine.getId()))
                .body(LineDto.of(savedLine));
    }
}
