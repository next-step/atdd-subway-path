package atdd.station.web;

import atdd.station.application.SubwayLineCommandService;
import atdd.station.application.SubwayLineQueryService;
import atdd.station.domain.SubwayLine;
import atdd.station.web.dto.SubwayLineCreateRequest;
import atdd.station.web.dto.SubwayLineResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/subway-lines")
public class SubwayLineController {
    private static final Logger logger = LoggerFactory.getLogger(SubwayLineController.class);

    private SubwayLineCommandService subwayLineCommandService;
    private SubwayLineQueryService subwayLineQueryService;

    public SubwayLineController(SubwayLineCommandService subwayLineCommandService,
                                SubwayLineQueryService subwayLineQueryService) {

        this.subwayLineCommandService = subwayLineCommandService;
        this.subwayLineQueryService = subwayLineQueryService;
    }

    @PostMapping
    public ResponseEntity<Void> createSubwayLine(@RequestBody SubwayLineCreateRequest subwayLineCreateRequest) {
        logger.info("[SubwayLineController.createSubwayLine] subwayLineCreateRequest={}", subwayLineCreateRequest);

        SubwayLine savedSubwayLine = subwayLineCommandService.create(subwayLineCreateRequest.getName());

        return ResponseEntity.created(URI.create("/subway-lines/" + savedSubwayLine.getId())).build();
    }

    @GetMapping
    public ResponseEntity<List<SubwayLineResponseDto>> getSubwayLines() {
        List<SubwayLineResponseDto> subwayLines = subwayLineQueryService.getSubwayLines()
                .stream()
                .map(subwayLine -> SubwayLineResponseDto.of(subwayLine))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(subwayLines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubwayLineResponseDto> getSubwayLine(@PathVariable Long id) {
        logger.info("[SubwayLineController.getSubwayLine] id={}", id);

        SubwayLine savedSubwayLine = subwayLineQueryService.getSubwayLine(id);

        return ResponseEntity.ok().body(SubwayLineResponseDto.of(savedSubwayLine));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubwayLine(@PathVariable Long id) {
        logger.info("[SubwayLineController.deleteSubwayLine] id={}", id);

        subwayLineCommandService.deleteSubwayLine(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.LOCATION, "/subway-lines")
                .build();
    }

}
