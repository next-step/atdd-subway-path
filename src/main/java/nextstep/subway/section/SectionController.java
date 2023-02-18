package nextstep.subway.section;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lines")
public class SectionController {

	private final SectionService sectionService;

	@PostMapping("/{id}/sections")
	public ResponseEntity<Void> createSection(@PathVariable Long id, @RequestBody SectionCreateRequest sectionRequest) {
		sectionService.addSection(id, sectionRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}/sections")
	public ResponseEntity<Void> deleteLine(@PathVariable Long id, @RequestParam Long stationId) {
		sectionService.deleteSectionById(id, stationId);
		return ResponseEntity.noContent().build();
	}
}
