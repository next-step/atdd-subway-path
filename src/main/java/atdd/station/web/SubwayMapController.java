package atdd.station.web;

import atdd.station.application.SubwayMapQueryService;
import atdd.station.application.dto.ShortestPathResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SubwayMapController.ROOT_URI)
public class SubwayMapController {
    public static final String ROOT_URI = "/subway-map";

    private SubwayMapQueryService subwayMapQueryService;

    public SubwayMapController(SubwayMapQueryService subwayMapQueryService) {
        this.subwayMapQueryService = subwayMapQueryService;
    }

    @GetMapping("/shortest-path")
    public ShortestPathResponseDto getShortestPath(@RequestParam Long startStationId,
                                                   @RequestParam Long destinationStationId) {

        return subwayMapQueryService.getShortestPath(startStationId, destinationStationId);
    }
    
}
