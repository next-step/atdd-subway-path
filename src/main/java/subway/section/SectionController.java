package subway.section;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{id}/sections")
    @ResponseStatus(HttpStatus.CREATED)
    public void generateSection(@PathVariable Long id, @RequestBody SectionCreateRequest request) {
        sectionService.saveSection(id, request);
    }

    @DeleteMapping("/lines/{id}/sections")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        sectionService.deleteSection(id, stationId);
    }
}
