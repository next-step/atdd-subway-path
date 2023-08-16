package subway.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import subway.domain.Path;
import subway.dto.PathResponse;
import subway.service.PathService;
import subway.validation.PathValidator;
import subway.validation.impl.PathSourceTargetEqualsValidator;
import subway.dto.PathRequest;

@RestController
@RequestMapping("/paths")
@RequiredArgsConstructor
public class PathController {

    private final PathService pathService;

    @GetMapping
    public ResponseEntity<PathResponse> findPath(@Valid PathRequest request) {
        PathValidator validator = new PathSourceTargetEqualsValidator(null); // validator chaining 가능
        validator.validate(request);

        Path path = pathService.findPath(request);
        return ResponseEntity.ok(PathResponse.from(path));
    }

}
