package subway.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import subway.application.in.StationRegisterUsecase;
import subway.application.query.response.StationResponse;

import java.net.URI;

@RestController
public class StationRegisterController {

    private final StationRegisterUsecase stationRegisterUsecase;

    public StationRegisterController(StationRegisterUsecase stationRegisterUsecase) {
        this.stationRegisterUsecase = stationRegisterUsecase;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody Request stationRegisterRequest) {
        StationRegisterUsecase.Command command = mapFrom(stationRegisterRequest);
        StationResponse station = stationRegisterUsecase.saveStation(command);
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(station);
    }

    public StationRegisterUsecase.Command mapFrom(Request request) {
        return new StationRegisterUsecase.Command(request.getName());
    }

    public static class Request {
        private String name;
        public String getName() {
            return name;
        }
    }
}
