package atdd.Edge;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping(value="/edge")
public class EdgeController {
    private final EdgeRepository edgeRepository;
    private final EdgeService edgeService;

    @PostMapping("")
    public ResponseEntity createEdge(@RequestBody EdgeCreateRequest edge){
        EdgeResponse savedEdge = edgeService.createEdge(edge);
        return ResponseEntity.created(URI.create("/edge/"+savedEdge.getId())).body(savedEdge);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteEdge(@PathVariable Long id){
        edgeService.deleteEdge(id);
        return ResponseEntity.noContent().build();
    }
}
