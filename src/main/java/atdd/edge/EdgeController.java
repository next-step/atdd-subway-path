package atdd.edge;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
