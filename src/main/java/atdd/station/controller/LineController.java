package atdd.station.controller;

import atdd.station.exception.ErrorType;
import atdd.station.exception.SubwayException;
import atdd.station.model.CreateEdgeRequestView;
import atdd.station.model.CreateLineRequestView;
import atdd.station.model.dto.LineResponseDto;
import atdd.station.model.entity.Edge;
import atdd.station.model.entity.Line;
import atdd.station.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<LineResponseDto> createLine(@RequestBody CreateLineRequestView view) {
        final Line line = lineService.create(view.toLine());

        LineResponseDto lineResponseDto = lineService.lineToLineDto(line);

        return ResponseEntity.created(URI.create("/lines/" + line.getId()))
                .body(lineResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<LineResponseDto>> findAllLines() {
        final List<Line> lines = lineService.findAll();

        List<LineResponseDto> lineResponseDtos = new ArrayList<>();

        for (Line line : lines)
            lineResponseDtos.add(lineService.lineToLineDto(line));

        return ResponseEntity.ok(lineResponseDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponseDto> findLine(@PathVariable long id) throws SubwayException {
        final Optional<Line> optionalLine = lineService.findById(id);

        if (optionalLine.isPresent()) {
            LineResponseDto lineResponseDto = lineService.lineToLineDto(optionalLine.get());

            return ResponseEntity.ok(lineResponseDto);
        }

        throw new SubwayException(ErrorType.NOT_FOUND_LINE);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable long id) {
        lineService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/edge")
    public ResponseEntity<LineResponseDto> addEdge(@PathVariable long id,
                                                   @RequestBody CreateEdgeRequestView view) throws SubwayException {
        final Edge.EdgeBuilder edgeBuilder = Edge.builder()
                .sourceStationId(view.getSourceStationId())
                .targetStationId(view.getTargetStationId());

        Optional<Line> lineOptional = lineService.addEdge(id, edgeBuilder.build());

        if (!lineOptional.isPresent()) {
            throw new SubwayException(ErrorType.NOT_FOUND_EDGE);
        }

        LineResponseDto lineResponseDto = lineService.lineToLineDto(lineOptional.get());

        return ResponseEntity.status(HttpStatus.CREATED).body(lineResponseDto);
    }

    @DeleteMapping("/{id}/edge")
    public ResponseEntity<LineResponseDto> deleteLine(@PathVariable long id,
                                                      @RequestParam Long stationId) {
        lineService.deleteEdge(id, stationId);

        return ResponseEntity.noContent().build();
    }
}
