package atdd.line;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(value="/lines")
public class LineController {
    private final LineRepository lineRepository;
    private final LineService lineService;


    @PostMapping("")
    public ResponseEntity createLine(@RequestBody LineCreateRequest line){
        Line savedLine = lineRepository.save(line.toEntity());
        return ResponseEntity.created(URI.create("/lines/"+savedLine.getId())).body(LineResponse.of(savedLine));
    }

    @GetMapping("/{id}")
    public ResponseEntity getLineById(@PathVariable Long id){
        LineDetailResponse lineDetailResponse = lineService.findLineById(id);
        return ResponseEntity.ok().body(lineDetailResponse);

    }

    @GetMapping("")
    public ResponseEntity getLines(){
        List<Line> lines = lineRepository.findAll();
        return ResponseEntity.ok().body(LineListResponse.of(lines));

    }
    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id){
        lineRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
