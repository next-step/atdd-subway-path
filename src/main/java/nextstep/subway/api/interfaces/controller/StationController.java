package nextstep.subway.api.interfaces.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;
import nextstep.subway.api.domain.dto.inport.StationCreateCommand;
import nextstep.subway.api.domain.service.impl.StationService;
import nextstep.subway.api.interfaces.dto.request.StationCreateRequest;
import nextstep.subway.api.interfaces.dto.response.StationResponse;

@RestController
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationCreateRequest stationCreateRequest) {
        StationResponse station = stationService.saveStation(StationCreateCommand.from(stationCreateRequest));
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(station);
    }

    @GetMapping(value = "/stations")
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(stationService.findAllStations());
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }
}
