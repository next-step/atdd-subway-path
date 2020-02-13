package atdd.station;

import atdd.station.entity.Line;
import atdd.station.usecase.LineDTO;
import atdd.station.usecase.LineUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class LineController {
  @Autowired
  LineUsecase lineService;

  @PostMapping("/lines")
  public ResponseEntity<LineDTO> addLine(@RequestBody LineDTO line) {
    LineDTO resultLine = lineService.addLine(line);
    return ResponseEntity.created(
        ServletUriComponentsBuilder
          .fromCurrentServletMapping()
        .path("/lines/{id}")
        .build()
        .expand(resultLine.getId())
        .toUri()
    ).body(resultLine);
  }
}
