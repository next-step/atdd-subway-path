package nextstep.subway.controller;

import java.util.List;
import nextstep.subway.facade.LineFacade;
import nextstep.subway.service.request.LineModifyRequest;
import nextstep.subway.service.request.LineRequest;
import nextstep.subway.service.response.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LineController {

    private final LineFacade lineFacade;

    public LineController(LineFacade lineFacade) {
        this.lineFacade = lineFacade;
    }

    @PostMapping("/lines")
    ResponseEntity<LineResponse> createStationLine(@RequestBody LineRequest request) {

        LineResponse response = lineFacade.lineCreate(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping("/lines")
    ResponseEntity<List<LineResponse>> getStationLineList() {

        return ResponseEntity.ok(lineFacade.findAllStationLines());
    }

    @GetMapping("/lines/{id}")
    ResponseEntity<LineResponse> getStationLine(@PathVariable long id) {

        return ResponseEntity.ok(lineFacade.lineFindById(id));
    }

    @PutMapping("/lines/{id}")
    ResponseEntity<Object> modifyStationLine(
        @PathVariable long id,
        @RequestBody LineModifyRequest request
    ) {

        lineFacade.modifyLine(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    ResponseEntity<Object> modifyStationLine(@PathVariable long id) {

        lineFacade.deleteLine(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
