package nextstep.subway.controller;

import nextstep.subway.facade.SectionFacade;
import nextstep.subway.service.response.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PathController {

    private final SectionFacade sectionFacade;

    public PathController(SectionFacade sectionFacade) {
        this.sectionFacade = sectionFacade;
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> getPath(
        @RequestParam long source, @RequestParam long target) {

        return ResponseEntity.ok(sectionFacade.getPath(source, target));
    }
}

