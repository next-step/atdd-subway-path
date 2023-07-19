package nextstep.subway.controller;

import java.net.URI;
import javax.websocket.server.PathParam;
import nextstep.subway.facade.SectionFacade;
import nextstep.subway.service.request.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SectionController {

    private final SectionFacade facade;

    public SectionController(SectionFacade facade) {
        this.facade = facade;
    }

    @PostMapping("/lines/{id}/sections")
    public ResponseEntity<Object> createStationLineSection(
        @PathVariable long id,
        @RequestBody SectionRequest request) {

        facade.addSection(id, request);
        return ResponseEntity.created(URI.create("/lines/" + id)).build();
    }

    @DeleteMapping("/lines/{id}/sections")
    public ResponseEntity<Object> deleteStationLineSection(
        @PathVariable(name = "id") long lineId,
        @PathParam("stationId") long stationId) {

        facade.deleteSection(lineId, stationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
