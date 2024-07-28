package nextstep.subway.line;

import nextstep.subway.line.dto.LineModifyRequest;
import nextstep.subway.line.dto.LineSectionAppendRequest;
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
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;

import java.net.URI;
import java.util.List;

@RequestMapping("/lines")
@RestController
public class LineController {

  private final LineService lineService;

  public LineController(LineService lineService) {
    this.lineService = lineService;
  }

  @PostMapping
  public ResponseEntity createLine(@RequestBody LineCreateRequest lineRequest) {
    Line line = lineService.createLine(lineRequest);
    return ResponseEntity.created(URI.create("/lines/"+ line.getId())).build();
  }

  @GetMapping
  public ResponseEntity<List<LineResponse>> getLines() {
    return ResponseEntity.ok().body(lineService.getLines());
  }

  @GetMapping("/{id}")
  public ResponseEntity<LineResponse> getLine(@PathVariable("id") Long id) {
    return ResponseEntity.ok().body(lineService.getLine(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<LineResponse> modifyLine(@PathVariable("id") Long id, @RequestBody LineModifyRequest lineModifyRequest) {
    return ResponseEntity.ok().body(lineService.modifyLine(id, lineModifyRequest));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteLine(@PathVariable("id") Long id) {
    lineService.deleteLine(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/sections")
  public ResponseEntity<Void> appendLineSection(@PathVariable("id") Long id, @RequestBody LineSectionAppendRequest lineSectionAppendRequest) {
    lineService.appendLineSection(id, lineSectionAppendRequest);
    return ResponseEntity.created(URI.create("/lines/"+ id + "/sections")).build();
  }

  @DeleteMapping("/{id}/sections")
  public ResponseEntity<Void> deleteLineSection(@PathVariable("id") Long id, @RequestParam("stationId") Long stationId) {
    lineService.deleteLineSection(id, stationId);
    return ResponseEntity.noContent().build();
  }
}
