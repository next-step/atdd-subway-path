package subway.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.application.in.SubwaySectionCloseUsecase;
import subway.domain.Station;
import subway.domain.SubwayLine;

@RestController
public class SubwaySectionCloseController {

    private final SubwaySectionCloseUsecase subwaySectioncloseUsecase;

    public SubwaySectionCloseController(SubwaySectionCloseUsecase subwaySectioncloseUsecase) {
        this.subwaySectioncloseUsecase = subwaySectioncloseUsecase;
    }

    @DeleteMapping("/subway-lines/{id}/sections")
    public ResponseEntity<Void> addSubwaySection(@PathVariable Long id, @RequestParam Long stationId) {
        SubwaySectionCloseUsecase.Command command = of(id, stationId);
        subwaySectioncloseUsecase.closeSection(command);
        return ResponseEntity.ok().build();
    }

    private static SubwaySectionCloseUsecase.Command of(Long id, Long stationId) {
        return new SubwaySectionCloseUsecase.Command(
                new SubwayLine.Id(id),
                new SubwaySectionCloseUsecase.Command.SubwaySection(new Station.Id(stationId)));
    }
}
