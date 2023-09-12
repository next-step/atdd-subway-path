package subway.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.application.command.in.SubwaySectionCloseUsecase;
import subway.application.command.validator.SubwaySectionCloseCommandValidator;
import subway.domain.Station;
import subway.domain.SubwayLine;

@RestController
@RequiredArgsConstructor
public class SubwaySectionCloseController {

    private final SubwaySectionCloseUsecase subwaySectioncloseUsecase;
    private final SubwaySectionCloseCommandValidator validator;


    @DeleteMapping("/subway-lines/{id}/sections")
    public ResponseEntity<Void> closeSubwaySection(@PathVariable Long id, @RequestParam Long stationId) {
        SubwaySectionCloseUsecase.Command command = of(id, stationId);
        subwaySectioncloseUsecase.closeSection(command);
        return ResponseEntity.ok().build();
    }

    private SubwaySectionCloseUsecase.Command of(Long id, Long stationId) {
        return new SubwaySectionCloseUsecase.Command(
                new SubwayLine.Id(id),
                new SubwaySectionCloseUsecase.Command.SectionCommand(new Station.Id(stationId)),
                validator);
    }
}
