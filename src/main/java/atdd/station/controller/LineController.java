package atdd.station.controller;

import atdd.exception.SubwayException;
import atdd.station.model.dto.CreateEdgeRequestView;
import atdd.station.model.dto.CreateLineRequestView;
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

@RestController
@RequestMapping("/lines")
public class LineController {
    @Autowired
    private LineService lineService;

    @PostMapping
    public ResponseEntity<LineResponseDto> createLine(@RequestBody CreateLineRequestView view) {
        final Line line = lineService.create(view.toLine());
        List<Edge> edges = lineService.getLineEdges(line);

        return ResponseEntity.created(URI.create("/lines/" + line.getId()))
                .body(line.toLineResponseDto(edges, lineService.getLineStations(line, edges)));
    }

    @GetMapping
    public ResponseEntity<List<LineResponseDto>> findAllLines() {
        final List<Line> lines = lineService.findAll();

        List<LineResponseDto> lineResponseDtos = new ArrayList<>();

        for (Line line : lines) {
            List<Edge> edges = lineService.getLineEdges(line);
            lineResponseDtos.add(line.toLineResponseDto(edges, lineService.getLineStations(line, edges)));
        }

        return ResponseEntity.ok(lineResponseDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponseDto> findLine(@PathVariable long id) throws SubwayException {
        final Line line = lineService.findById(id);
        List<Edge> edges = lineService.getLineEdges(line);

        return ResponseEntity.ok(line.toLineResponseDto(edges, lineService.getLineStations(line, edges)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable long id) {
        lineService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/edges")
    public ResponseEntity<LineResponseDto> addEdge(@PathVariable long id,
                                                   @RequestBody CreateEdgeRequestView view) throws SubwayException {
        Line line = lineService.addEdge(id, view.toEdge());
        List<Edge> edges = lineService.getLineEdges(line);

        return ResponseEntity.status(HttpStatus.CREATED).body(line.toLineResponseDto(edges, lineService.getLineStations(line, edges)));
    }

    @DeleteMapping("/{id}/edges")
    public ResponseEntity<LineResponseDto> deleteStation(@PathVariable long id,
                                                      @RequestParam Long stationId) {
        lineService.deleteStation(id, stationId);

        return ResponseEntity.noContent().build();
    }
}
