package nextstep.subway.section;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/lines/{lineId}/sections")
@RestController
public class SectionController {

  private final SectionService sectionService;

  public SectionController(SectionService sectionService) {
    this.sectionService = sectionService;
  }

  @PostMapping
  public ResponseEntity<Void> createSection(@PathVariable Long lineId, @RequestBody SectionRequest request) {
    sectionService.createSection(lineId, request);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{stationId}")
  public ResponseEntity<Void> deleteSectionByStation(@PathVariable Long lineId, @PathVariable Long stationId) {
    sectionService.deleteSection(lineId, stationId);
    return ResponseEntity.ok().build();
  }
}
