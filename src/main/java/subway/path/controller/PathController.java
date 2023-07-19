package subway.path.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.path.dto.PathRetrieveResponse;
import subway.path.service.PathService;

@RestController
@RequestMapping("/path")
@RequiredArgsConstructor
public class PathController {
    private final PathService pathService;

    @GetMapping
    public ResponseEntity<PathRetrieveResponse> getPath(@RequestParam Long source, @RequestParam Long target) {
        PathRetrieveResponse response = pathService.getShortestPath(source, target);
        return ResponseEntity.ok().body(response);
    }
}
