package nextstep.subway.path.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.path.application.PathService;
import nextstep.subway.path.application.ShortestPathSearchType;
import nextstep.subway.path.dto.PathResponse;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> findShortestPath(@RequestParam("source") Long startId,
        @RequestParam("target") Long endId, @RequestParam("type") String typeString) {
        ShortestPathSearchType type = determineTypeByRequestParam(typeString);
        return ResponseEntity.ok().body(pathService.findShortestPath(startId, endId, type));
    }

    private ShortestPathSearchType determineTypeByRequestParam(String typeString) {
        return ShortestPathSearchType.findTypeByTypeName(typeString);
    }
}
