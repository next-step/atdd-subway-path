package subway.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import subway.application.query.in.SubwayLineDetailQuery;
import subway.application.response.SubwayLineResponse;
import subway.domain.SubwayLine;

@RestController
public class SubwayLineDetailQueryController {

    private final SubwayLineDetailQuery subwayLineDetailQuery;

    public SubwayLineDetailQueryController(SubwayLineDetailQuery subwayLineDetailQuery) {
        this.subwayLineDetailQuery = subwayLineDetailQuery;
    }

    @GetMapping("/subway-lines/{id}")
    ResponseEntity<SubwayLineResponse> findOne(@PathVariable Long id) {
        SubwayLineDetailQuery.Command command = mapFrom(id);
        SubwayLineResponse response = subwayLineDetailQuery.findOne(command);
        return ResponseEntity.ok().body(response);
    }

    private SubwayLineDetailQuery.Command mapFrom(Long id) {
        SubwayLine.Id domainId = new SubwayLine.Id(id);
        return new SubwayLineDetailQuery.Command(domainId);
    }
}
