package subway.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import subway.application.in.StationCloseUsecase;
import subway.domain.Station;

@RestController
class StationCloseController {
    private final StationCloseUsecase stationCloseUsecase;

    StationCloseController(StationCloseUsecase stationCloseUsecase) {
        this.stationCloseUsecase = stationCloseUsecase;
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationCloseUsecase.closeStation(new StationCloseUsecase.Command(new Station.Id(id)));
        return ResponseEntity.noContent().build();
    }
}
