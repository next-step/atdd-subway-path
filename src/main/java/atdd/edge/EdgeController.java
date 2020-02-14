package atdd.edge;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("edges")
public class EdgeController {

    private final EdgeRepository edgeRepository;

    public EdgeController(EdgeRepository edgeRepository) {
        this.edgeRepository = edgeRepository;
    }

    @PostMapping("")
    public ResponseEntity createEdge(@RequestBody EdgeDto requestEdge) {
        Edge savedEdge = edgeRepository.save(requestEdge.toEdge());
        return ResponseEntity.created(URI.create("/edges/" + savedEdge.getId()))
                .body(EdgeDto.of(savedEdge));
    }

    @DeleteMapping("{lineId}/{stationId}")
    public ResponseEntity deleteEdge(@PathVariable Long lineId, @PathVariable Long stationId) {

        // TODO

        return ResponseEntity.noContent().build();
    }
}
