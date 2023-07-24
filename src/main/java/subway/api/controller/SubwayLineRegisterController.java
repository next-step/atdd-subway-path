package subway.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import subway.application.in.SubwayLineRegisterUsecase;
import subway.application.query.response.SubwayLineResponse;
import subway.domain.Kilometer;
import subway.domain.Station;

@RestController
class SubwayLineRegisterController {

    private final SubwayLineRegisterUsecase subwayLineRegisterUsecase;

    public SubwayLineRegisterController(SubwayLineRegisterUsecase subwayLineRegisterUsecase) {
        this.subwayLineRegisterUsecase = subwayLineRegisterUsecase;
    }

    @PostMapping("/subway-lines")
    public ResponseEntity<SubwayLineResponse> createStation(@RequestBody Request request) {
        SubwayLineRegisterUsecase.Command command = request.mapFrom();
        SubwayLineResponse subwayLineResponse = subwayLineRegisterUsecase.registerSubwayLine(command);
        return ResponseEntity.ok().body(subwayLineResponse);
    }


    static class Request {
        private String name;
        private String color;
        private Long upStationId;
        private Long downStationId;
        private int distance;

        public Request(String name, String color, Long upStationId, Long downStationId, int distance) {
            this.name = name;
            this.color = color;
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }

        SubwayLineRegisterUsecase.Command mapFrom() {
            return new SubwayLineRegisterUsecase.Command(
                    name,
                    color,
                    new Station.Id(upStationId),
                    new Station.Id(downStationId),
                    Kilometer.of(distance));
        }
    }
}