package atdd.station.controller;

import atdd.station.model.CreateLineRequestView;
import atdd.station.model.entity.Line;
import atdd.station.repository.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
}
