package subway.api.controller;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import subway.application.command.in.SubwaySectionAddUsecase;
import subway.application.command.validator.SubwaySectionAddCommandValidator;
import subway.domain.Kilometer;
import subway.domain.Station;
import subway.domain.SubwayLine;

@RestController
@RequiredArgsConstructor
public class SubwaySectionAddController {

    private final SubwaySectionAddUsecase subwaySectionAddUsecase;
    private final SubwaySectionAddCommandValidator validator;

    @PostMapping("/subway-lines/{id}/sections")
    public ResponseEntity<Void> addSubwaySection(@PathVariable Long id, @RequestBody Request request) {
        SubwaySectionAddUsecase.Command command = mapFrom(request, id);
        subwaySectionAddUsecase.addSubwaySection(command);
        return ResponseEntity.ok().build();
    }

    SubwaySectionAddUsecase.Command mapFrom(SubwaySectionAddController.Request request, Long id) {
        return SubwaySectionAddUsecase.Command.builder()
                .subwayLineId(new SubwayLine.Id(id))
                .subwaySection(
                        SubwaySectionAddUsecase.Command.SectionCommand.builder()
                                .upStationId(new Station.Id(request.getUpStationId()))
                                .downStationId(new Station.Id(request.getDownStationId()))
                                .distance(Kilometer.of(request.getDistance()))
                                .build())
                .validator(validator)
                .build();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        private Long upStationId;
        private Long downStationId;
        private int distance;

        public Request(Long upStationId, Long downStationId, int distance) {
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }
    }
}
