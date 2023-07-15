package subway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.service.StationPathService;
import subway.service.dto.StationPathResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/paths")
public class StationPathController {
    private final StationPathService stationPathService;

    @GetMapping
    public ResponseEntity<StationPathResponse> getStationPath(
            @RequestParam("source") Long startStationId,
            @RequestParam("target") Long destinationStationId) {
        final StationPathResponse response = stationPathService.searchStationPath(startStationId, destinationStationId);

        return ResponseEntity.ok(response);
    }
}
