package atdd.station.controller;

import atdd.station.model.CreateLineRequestView;
import atdd.station.model.entity.Line;
import atdd.station.repository.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/lines")
public class LineController {
    @Autowired
    private LineRepository lineRepository;

    @PostMapping
    public ResponseEntity<Line> createLine(@RequestBody CreateLineRequestView view) {
        final Line line = lineRepository.save(view.toLine());

        return ResponseEntity.created(URI.create("/lines/" + line.getId()))
                .body(line);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Line> findLine(@PathVariable long id) {
        final Optional<Line> optionalLine = lineRepository.findById(id);


        if(optionalLine.isPresent())
            return ResponseEntity
                    .ok(optionalLine.get());

        return ResponseEntity
                .notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Line> deleteLine(@PathVariable long id) {
        lineRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
