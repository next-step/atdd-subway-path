package atdd.path.controller;

import atdd.path.domain.Edge;
import atdd.path.domain.Edges;
import atdd.path.domain.Line;
import atdd.path.domain.Station;
import atdd.path.domain.dto.EdgeDto;
import atdd.path.repository.EdgeRepository;
import atdd.path.repository.LineRepository;
import atdd.path.repository.StationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("edges")
public class EdgeController {

    private final EdgeRepository edgeRepository;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public EdgeController(EdgeRepository edgeRepository, LineRepository lineRepository, StationRepository stationRepository) {
        this.edgeRepository = edgeRepository;
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @PostMapping("")
    public ResponseEntity createEdge(@RequestBody EdgeDto requestEdge) {
        Edge savedEdge = edgeRepository.save(requestEdge.toEdge());
        return ResponseEntity.created(URI.create("/edges/" + savedEdge.getId()))
                .body(EdgeDto.of(savedEdge));
    }

    @DeleteMapping("{lineId}/{stationId}")
    public ResponseEntity deleteEdge(@PathVariable Long lineId, @PathVariable Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new RuntimeException());
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException());

        List<Edge> oldEdges = line.getEdges();
        Edges edges = new Edges(line.getEdges());
        edges.removeStation(station);

        Edge newEdge = edges.getEdges().stream()
                .filter(it -> !oldEdges.contains(it))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        newEdge.updateLine(line);

        line.getEdges().stream()
                .filter(it -> it.hasStation(station))
                .forEach(it -> edgeRepository.delete(it));
        edgeRepository.save(newEdge);

        return ResponseEntity.noContent().build();
    }
}
