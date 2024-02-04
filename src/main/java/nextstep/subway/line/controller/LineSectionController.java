package nextstep.subway.line.controller;

import nextstep.subway.line.dto.SectionCreateRequest;
import nextstep.subway.line.service.LineSectionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class LineSectionController {

    private final LineSectionService lineSectionService;

    public LineSectionController(LineSectionService lineSectionService) {
        this.lineSectionService = lineSectionService;
    }

    @PostMapping("/lines/{id}/sections")
    @ResponseStatus(HttpStatus.CREATED)
    public void generateSection(@PathVariable Long id, @RequestBody SectionCreateRequest request) {
        lineSectionService.saveSection(id, request);
    }

    @DeleteMapping("/lines/{id}/sections")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        lineSectionService.deleteSection(id, stationId);
    }
}
