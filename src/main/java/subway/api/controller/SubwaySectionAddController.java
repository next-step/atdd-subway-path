package subway.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import subway.application.in.SubwaySectionAddUsecase;
import subway.domain.Kilometer;
import subway.domain.Station;
import subway.domain.SubwayLine;

@RestController
public class SubwaySectionAddController {

    private final SubwaySectionAddUsecase subwaySectionAddUsecase;

    public SubwaySectionAddController(SubwaySectionAddUsecase subwaySectionAddUsecase) {
        this.subwaySectionAddUsecase = subwaySectionAddUsecase;
    }

    @PostMapping("/subway-lines/{id}/sections")
    public ResponseEntity<Void> addSubwaySection(@PathVariable Long id, @RequestBody Request request) {
        SubwaySectionAddUsecase.Command command = request.mapFrom(id);
        subwaySectionAddUsecase.addSubwaySection(command);
        return ResponseEntity.ok().build();
    }

    public static class Request {
        private Long upStationId;
        private Long downStationId;
        private int distance;


        public Request(Long upStationId, Long downStationId, int distance) {
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }
        SubwaySectionAddUsecase.Command mapFrom(Long id) {
            return new SubwaySectionAddUsecase.Command(
                    new SubwayLine.Id(id),
                    new SubwaySectionAddUsecase.Command.SubwaySection(
                            new Station.Id(upStationId),
                            new Station.Id(downStationId),
                            Kilometer.of(distance)));
        }
    }
}
