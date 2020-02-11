package atdd.station.web;

import atdd.station.application.SubwaySectionCommandService;
import atdd.station.domain.SubwaySection;
import atdd.station.web.dto.SubwaySectionCreateRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(SubwaySectionController.ROOT_URI)
public class SubwaySectionController {
    private static final Logger logger = LoggerFactory.getLogger(SubwaySectionController.class);

    public static final String ROOT_URI = "/subway-sections";

    private SubwaySectionCommandService subwaySectionCommandService;

    public SubwaySectionController(SubwaySectionCommandService subwaySectionCommandService) {
        this.subwaySectionCommandService = subwaySectionCommandService;
    }

    @PostMapping("/{subwayLineId}")
    public ResponseEntity<Void> createSubwaySection(@PathVariable Long subwayLineId,
                                                    @RequestBody SubwaySectionCreateRequestDto subwaySectionCreateRequestDto) {
        logger.info("[SubwaySectionController.createSubwaySection] subwayLineId={}, SubwaySectionCreateRequestDto={}", subwayLineId, subwaySectionCreateRequestDto);

        SubwaySection savedSubwaySection = subwaySectionCommandService.createSubwaySection(subwayLineId, subwaySectionCreateRequestDto.getSourceStationId(), subwaySectionCreateRequestDto.getTargetStationId());

        return ResponseEntity.created(URI.create(ROOT_URI + "/" + savedSubwaySection.getId())).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteStationOfSubwaySection(@RequestParam Long subwayLineId,
                                                             @RequestParam String stationName) {
        logger.info("[SubwaySectionController.deleteStationOfSubwaySection] subwayLineId={}, stationName={}", subwayLineId, stationName);

        subwaySectionCommandService.deleteStationOfSubwaySection(subwayLineId, stationName);

        return ResponseEntity.ok()
                .header(HttpHeaders.LOCATION, SubwayLineController.ROOT_URI)
                .build();
    }

}
