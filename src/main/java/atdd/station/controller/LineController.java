package atdd.station.controller;

import atdd.station.exception.ErrorType;
import atdd.station.exception.SubwayException;
import atdd.station.model.CreateEdgeRequestView;
import atdd.station.model.CreateLineRequestView;
import atdd.station.model.dto.LineDto;
import atdd.station.model.entity.Edge;
import atdd.station.model.entity.Line;
import atdd.station.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lines")
public class LineController {
    @Autowired
    private LineService lineService;

    @PostMapping
    public ResponseEntity<LineDto> createLine(@RequestBody CreateLineRequestView view) {
        final Line line = lineService.create(view.toLine());

        LineDto lineDto = lineService.lineToLineDto(line);

        return ResponseEntity.created(URI.create("/lines/" + line.getId()))
                .body(lineDto);
    }

    @GetMapping
    public ResponseEntity<List<LineDto>> findAllLines() {
        final List<Line> lines = lineService.findAll();

        List<LineDto> lineDtos = new ArrayList<>();

        for (Line line : lines)
            lineDtos.add(lineService.lineToLineDto(line));

        return ResponseEntity.ok(lineDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineDto> findLine(@PathVariable long id) throws SubwayException {
        final Optional<Line> optionalLine = lineService.findById(id);

        if (optionalLine.isPresent()) {
            LineDto lineDto = lineService.lineToLineDto(optionalLine.get());

            return ResponseEntity.ok(lineDto);
        }

        throw new SubwayException(ErrorType.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable long id) {
        lineService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/edge")
    public ResponseEntity<LineDto> addEdge(@PathVariable long id,
                                           @RequestBody CreateEdgeRequestView view) throws SubwayException {
        final Edge.EdgeBuilder edgeBuilder = Edge.builder()
                .sourceStationId(view.getSourceStationId())
                .targetStationId(view.getTargetStationId());

        Optional<Line> lineOptional = lineService.addEdge(id, edgeBuilder.build());

        if (!lineOptional.isPresent()) {
            throw new SubwayException(ErrorType.NOT_FOUND);
        }

        LineDto lineDto = lineService.lineToLineDto(lineOptional.get());

        return ResponseEntity.ok(lineDto);
    }

    @DeleteMapping("/{id}/edge")
    public ResponseEntity<LineDto> deleteLine(@PathVariable long id,
                                              @RequestParam Long stationId) {
        lineService.deleteEdge(id, stationId);

        return ResponseEntity.noContent().build();
    }
}
