package nextstep.subway.path.ui;

import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PathController {

    @RequestMapping("/paths")
    public ResponseEntity getShortestPath(@RequestParam Long sourceId, @RequestParam Long targetId) {
        List<StationResponse> stations = new ArrayList<>();
        stations.add(new StationResponse(1L,"강남역", LocalDateTime.now(),LocalDateTime.now()));
        stations.add(new StationResponse(3L,"교대역", LocalDateTime.now(),LocalDateTime.now()));
        stations.add(new StationResponse(4L,"남부터미널역", LocalDateTime.now(),LocalDateTime.now()));

        PathResponse response = new PathResponse(stations, 13);

        return ResponseEntity.ok().body(response);
    }
}
