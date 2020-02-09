package atdd.station.web;

import atdd.station.application.SubwayLineCommandService;
import atdd.station.domain.SubwayLine;
import atdd.station.web.dto.SubwayLineCreateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/subway-lines")
public class SubwayLineController {
    private static final Logger logger = LoggerFactory.getLogger(SubwayLineController.class);

    private SubwayLineCommandService subwayLineCommandService;

    public SubwayLineController(SubwayLineCommandService subwayLineCommandService) {
        this.subwayLineCommandService = subwayLineCommandService;
    }

    @PostMapping
    public ResponseEntity<Void> createSubwayLine(@RequestBody SubwayLineCreateRequest subwayLineCreateRequest) {
        logger.info("[subwayLineCommandService.createSubwayLine] subwayLineCreateRequest={}", subwayLineCreateRequest);

        SubwayLine savedSubwayLine = subwayLineCommandService.create(subwayLineCreateRequest.getName());

        return ResponseEntity.created(URI.create("/subway-lines/" + savedSubwayLine.getId())).build();
    }
}
