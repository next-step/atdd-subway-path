package nextstep.subway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nextstep.subway.dto.SubwayLineRequest;
import nextstep.subway.dto.SubwayLineResponse;
import nextstep.subway.dto.SubwayLineUpdateRequest;
import nextstep.subway.service.SubwayLineService;

import java.util.List;

@RestController
@RequestMapping("/lines")
@RequiredArgsConstructor
public class SubwayLineController {
    private final SubwayLineService subwayLineService;

    @PostMapping
    ResponseEntity<SubwayLineResponse> createSubwayLine(@RequestBody SubwayLineRequest request) {
        var response = subwayLineService.saveSubwayLine(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    ResponseEntity<List<SubwayLineResponse>> showSubwayLines() {
        var response = subwayLineService.findAllSubwayLines();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    ResponseEntity<SubwayLineResponse> showSubwayLine(@PathVariable Long id) {
        var response = subwayLineService.findSubwayLine(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> updateSubwayLine(@PathVariable Long id, @RequestBody SubwayLineUpdateRequest request) {
        subwayLineService.updateSubwayLine(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteSubwayLine(@PathVariable Long id) {
        subwayLineService.deleteSubwayLine(id);
        return ResponseEntity.noContent().build();
    }


}
