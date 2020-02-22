package atdd.station.controller;

import atdd.station.exception.ErrorType;
import atdd.station.exception.SubwayException;
import atdd.station.model.CreateEdgeRequestView;
import atdd.station.model.CreateLineRequestView;
import atdd.station.model.entity.Edge;
import atdd.station.model.entity.Line;
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
    private LineService lineService;

    @PostMapping
    public ResponseEntity<Line> createLine(@RequestBody CreateLineRequestView view) {
        final Line line = lineService.create(view.toLine());

        return ResponseEntity.created(URI.create("/lines/" + line.getId()))
                .body(line);
    }

    @GetMapping
    public ResponseEntity<List<Line>> findAllLines() {
        final List<Line> lines = lineService.findAll();

        for (Line line : lines)
            lineService.stationDtos(line);

        return ResponseEntity.ok(lines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Line> findLine(@PathVariable long id) throws SubwayException {

        final Optional<Line> optionalLine = lineService.findById(id);

        if (optionalLine.isPresent()) {
            lineService.stationDtos(optionalLine.get());

            return ResponseEntity.ok(optionalLine.get());
        }

        throw new SubwayException(ErrorType.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable long id) {
        lineService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/edge")
    public ResponseEntity<Line> addEdge(@PathVariable long id,
                                        @RequestBody CreateEdgeRequestView view) throws SubwayException {
        final Edge.EdgeBuilder edgeBuilder = Edge.builder()
                .sourceStationId(view.getSourceStationId())
                .targetStationId(view.getTargetStationId());

        Optional<Line> line = lineService.addEdge(id, edgeBuilder.build());

        if(!line.isPresent()) {
            throw new SubwayException(ErrorType.NOT_FOUND);
        }

        return ResponseEntity.ok(line.get());
    }

    @DeleteMapping("/{id}/edge")
    public ResponseEntity<Line> deleteLine(@PathVariable long id,
                                           @RequestParam Long stationId) {
        lineService.deleteEdge(id, stationId);

        return ResponseEntity.noContent().build();
    }
}
