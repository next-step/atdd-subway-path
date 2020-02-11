package atdd.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(value="/lines")
public class LineController {
    private final LineRepository lineRepository;

    public LineController(LineRepository lineRepository){
        this.lineRepository = lineRepository;
    }

    @PostMapping("")
    public ResponseEntity createLine(@RequestBody LineCreateRequest line){
        Line savedLine = lineRepository.save(line.toEntity());
        return ResponseEntity.created(URI.create("/lines/"+savedLine.getId())).body(LineResponse.of(savedLine));
    }
}
