package atdd.station.controller;

import atdd.station.model.CreateEdgeRequestView;
import atdd.station.model.CreateLineRequestView;
import atdd.station.model.entity.Line;
import atdd.station.repository.LineRepository;
import atdd.station.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lines")
public class LineController {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @PostMapping
    public ResponseEntity<Line> createLine(@RequestBody CreateLineRequestView view) {
        final Line line = lineRepository.save(view.toLine());

        return ResponseEntity.created(URI.create("/lines/" + line.getId()))
                .body(line);
    }

    @GetMapping
    public ResponseEntity<List<Line>> findAllLines() {
        final List<Line> optionalLine = lineRepository.findAll();

        return ResponseEntity
                .ok(optionalLine);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Line> findLine(@PathVariable long id) {

        final Optional<Line> optionalLine = lineService.findLine(id);

        if (optionalLine.isPresent())
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

    @PostMapping("/{id}/edge")
    public ResponseEntity<Line> addEdge(@PathVariable long id,
                                        @RequestBody CreateEdgeRequestView view) {
        Optional<Line> line = lineService.addEdge(id, view.getSourceStationId(), view.getTargetStationId());

        if (line.isPresent())
            return ResponseEntity.ok(line.get());

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}/edge")
    public ResponseEntity<Line> deleteLine(@PathVariable long id,
                                           @RequestParam Long stationId) {
        lineService.deleteEdge(id, stationId);

        return ResponseEntity.noContent().build();
    }
}
