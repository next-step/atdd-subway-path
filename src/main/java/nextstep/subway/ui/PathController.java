package nextstep.subway.ui;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.FindPathRequest;
import nextstep.subway.applicaion.dto.FindPathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("paths")
public class PathController {

  @GetMapping
  public ResponseEntity<FindPathResponse> findPath(@Valid FindPathRequest request) {
    return ResponseEntity.ok().build();
  }

}
