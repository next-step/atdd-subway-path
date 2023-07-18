package subway.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import subway.dto.request.SectionRequest;
import subway.dto.response.LineResponse;
import subway.service.SectionService;

@RequestMapping("/lines/{id}/sections")
@RestController
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @PostMapping
    public ResponseEntity<LineResponse> addSection(@PathVariable Long id, @RequestBody SectionRequest request) {
        LineResponse line = sectionService.addSection(id, request);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSection(@PathVariable Long id, Long stationId) {
        sectionService.deleteSection(id, stationId);
        return ResponseEntity.noContent().build();
    }

}
