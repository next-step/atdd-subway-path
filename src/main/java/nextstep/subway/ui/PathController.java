package nextstep.subway.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;

@RestController
public class PathController {

    @GetMapping("/paths")
    public ResponseEntity<Map<String, Object>> findPath(@RequestParam Long source, @RequestParam Long target) {
        List<StationResponse> stations = new ArrayList<>();
        stations.add(new StationResponse(new Station(1L, "교대역")));
        stations.add(new StationResponse(new Station(4L, "남부터미널역")));
        stations.add(new StationResponse(new Station(3L, "양재역")));

        Map<String, Object> response = new HashMap<>();
        response.put("stations", stations);
        response.put("distance", 5);

        return ResponseEntity.ok(response);
    }
}
