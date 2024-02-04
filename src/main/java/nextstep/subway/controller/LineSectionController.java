package nextstep.subway.controller;

import nextstep.subway.controller.dto.SectionAddRequest;
import nextstep.subway.service.SectionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/lines/{lineId}/sections")
@RestController
public class LineSectionController {

    private final SectionService service;

    public LineSectionController(SectionService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void addSection(
        @PathVariable Long lineId,
        @RequestBody SectionAddRequest request
    ) {
        service.addSection(lineId, request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void deleteSection(
        @PathVariable Long lineId,
        @RequestParam Long stationId
    ) {
        service.deleteSection(lineId, stationId);
    }
}
