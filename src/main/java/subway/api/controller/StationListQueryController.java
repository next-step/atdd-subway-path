package subway.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import subway.application.query.in.StationListQuery;
import subway.application.response.StationResponse;

import java.util.List;

@RestController
class StationListQueryController {

    private final StationListQuery stationListQuery;

    public StationListQueryController(StationListQuery stationListQuery) {
        this.stationListQuery = stationListQuery;
    }

    @GetMapping(value = "/stations")
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(stationListQuery.findAll());
    }
}
