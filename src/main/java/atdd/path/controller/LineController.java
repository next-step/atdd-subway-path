package atdd.path.controller;

import atdd.path.domain.Line;
import atdd.path.domain.dto.LineDto;
import atdd.path.repository.LineRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("lines")
public class LineController {

    private final LineRepository lineRepository;

    public LineController(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @PostMapping("")
    public ResponseEntity createLine(@RequestBody LineDto requestLine) {
        Line savedLine = lineRepository.save(requestLine.toLine());
        return ResponseEntity.created(URI.create("/lines/" + savedLine.getId()))
                .body(LineDto.of(savedLine));
    }

    @GetMapping("")
    public ResponseEntity findLine() {
        List<Line> lineList = lineRepository.findAll();

        return ResponseEntity.ok(LineDto.listOf(lineList));
    }

    @GetMapping("{id}")
    public ResponseEntity findLineById(@PathVariable Long id) {
        return lineRepository.findById(id)
                .map(it -> ResponseEntity.ok(LineDto.of(it)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteLineById(@PathVariable Long id) {
        lineRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
