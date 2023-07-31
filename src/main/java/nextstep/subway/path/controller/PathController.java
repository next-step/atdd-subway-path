package nextstep.subway.path.controller;

import lombok.RequiredArgsConstructor;
import nextstep.subway.path.dto.request.PathRequest;
import nextstep.subway.path.dto.response.PathResponse;
import nextstep.subway.path.service.PathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class PathController {

    private final PathService pathService;

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> showShortestDistance(@Valid PathRequest pathRequest) {
        PathResponse pathResponse = pathService.getShortestDistance(pathRequest);

        return ResponseEntity.ok(pathResponse);
    }

}
