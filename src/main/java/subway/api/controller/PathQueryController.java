package subway.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.application.command.in.SubwaySectionCloseUsecase;
import subway.application.query.in.PathQuery;
import subway.application.query.validator.PathQueryCommandValidator;
import subway.application.response.PathResponse;
import subway.domain.PathStation;
import subway.domain.Station;
import subway.domain.SubwayLine;

@RestController
@RequiredArgsConstructor
public class PathQueryController {

    private final PathQuery pathQuery;
    private final PathQueryCommandValidator validator;

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> closeSubwaySection(@RequestParam Long source, @RequestParam Long target) {
        PathQuery.Command command = of(source, target);
        PathResponse response = pathQuery.findOne(command);
        return ResponseEntity.ok().body(response);
    }

    private PathQuery.Command of(Long source, Long target) {
        return PathQuery.Command.builder()
                .startStationId(PathStation.Id.of(source))
                .endStationId(PathStation.Id.of(target))
                .validator(validator)
                .build();
    }
}
