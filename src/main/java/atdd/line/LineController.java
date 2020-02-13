package atdd.line;

`import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
}
