package nextstep.subway.line.ui;
import java.util.List;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity getLines() {
       List<LineResponse> lineResponseList =  lineService.getLines();
       return ResponseEntity.ok().body(lineResponseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity getLine(@PathVariable long id) {
       LineResponse lineResponse = lineService.findLineById(id);
       return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity modifyLine(@PathVariable long id, @RequestBody LineRequest lineRequest) {
      LineResponse lineResponse = lineService.modifyLine(id,lineRequest);
      return ResponseEntity.accepted().body(lineResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity removeLine(@PathVariable long id) {
      lineService.removeLine(id);
      return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity addSection(@PathVariable long lineId, @RequestBody SectionRequest sectionRequest) {
      lineService.addSection(lineId,sectionRequest);
      return ResponseEntity.created(URI.create("/lines/" + lineId)).build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity removeSection(@PathVariable long lineId,@RequestParam long stationId){
      lineService.removeSection(lineId,stationId);
      return ResponseEntity.noContent().build();
    }
}
