package nextstep.subway.line.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.application.SectionService;
import nextstep.subway.line.application.dto.request.SectionRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lines")
public class SectionController {
    private final SectionService sectionService;

    @PostMapping("/{lineId}/sections")
    public void addSection(@PathVariable Long lineId, @RequestBody @Valid SectionRequest sectionRequest) {
        sectionService.addSection(lineId, sectionRequest);
    }

    @DeleteMapping("/{lineId}/sections")
    public void deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.deleteSection(lineId, stationId);
    }
}
