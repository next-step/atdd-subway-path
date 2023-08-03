package subway.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import subway.application.query.in.SubwayLineListQuery;
import subway.application.response.SubwayLineResponse;

import java.util.List;

@RestController
class SubwayLineListQueryController {

    private final SubwayLineListQuery subwayLineListQuery;

    public SubwayLineListQueryController(SubwayLineListQuery subwayLineListQuery) {
        this.subwayLineListQuery = subwayLineListQuery;
    }

    @GetMapping("/subway-lines")
    ResponseEntity<List<SubwayLineResponse>> findAll() {
        List<SubwayLineResponse> subwayLineRespons = subwayLineListQuery.findAll();
        return ResponseEntity.ok().body(subwayLineRespons);
    }
}
