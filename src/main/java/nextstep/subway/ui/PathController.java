package nextstep.subway.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathDto;
import nextstep.subway.applicaion.dto.request.PathRequest;
import nextstep.subway.applicaion.dto.response.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/paths" )
public class PathController {

    private final PathService pathService;

    @GetMapping
    public ResponseEntity<PathResponse> findShortPath(@Valid PathRequest pathRequest) {
        PathDto pathDto = pathService.findShortPath(pathRequest);
        return ResponseEntity.ok().body(PathResponse.from(pathDto));
    }
}
