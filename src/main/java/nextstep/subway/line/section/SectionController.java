package nextstep.subway.line.section;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping("{sectionId}")
    public  ResponseEntity<SectionResponse> getSection(@PathVariable long sectionId){
        return ResponseEntity.ok().body(SectionResponse.of(sectionService.getSection(sectionId)));
    }
}
