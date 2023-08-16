package subway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.domain.Path;
import subway.dto.PathResponse;
import subway.service.PathService;
import subway.validation.PathValidator;
import subway.validation.impl.PathSourceTargetEqualsValidator;
import subway.validation.request.PathRequest;

@RestController
@RequestMapping("/paths")
@RequiredArgsConstructor
public class PathController {

    private final PathService pathService;

    @GetMapping
    public ResponseEntity<PathResponse> findPath(@RequestParam Long source, @RequestParam Long target) {
        PathValidator validator = new PathSourceTargetEqualsValidator(null); // validator chaining 가능
        validator.validate(new PathRequest(source, target));

        Path path = pathService.findPath(source, target);
        return ResponseEntity.ok(PathResponse.from(path));
    }

}
