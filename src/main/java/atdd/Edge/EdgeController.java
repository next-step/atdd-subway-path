package atdd.Edge;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(value="/edge")
public class EdgeController {
    private final EdgeRepository edgeRepository;

    public EdgeController(EdgeRepository edgeRepository){this.edgeRepository = edgeRepository;}

    @PostMapping("")
    public ResponseEntity createEdge(@RequestBody EdgeCreateRequest edge){
        Edge savedEdge = edgeRepository.save(edge.toEntity());
        return ResponseEntity.created(URI.create("/edge/"+savedEdge.getId())).body(EdgeResponse.of(savedEdge));
    }
}
