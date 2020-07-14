package nextstep.subway.path.ui;

import com.google.common.collect.Lists;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/paths")
public class PathController {

    @GetMapping("/shortest")
    public ResponseEntity<PathResponse> findShortestPath(@RequestParam("startStationId") Long startStationId, @RequestParam("endStationId") Long endStationId) {
        return ResponseEntity.ok(PathResponse.of(
                Lists.newArrayList(
                        new StationResponse(1L, "양재시민의숲역", LocalDateTime.now(), LocalDateTime.now()),
                        new StationResponse(2L, "양재역", LocalDateTime.now(), LocalDateTime.now()),
                        new StationResponse(3L, "강남역", LocalDateTime.now(), LocalDateTime.now()),
                        new StationResponse(4L, "역삼역", LocalDateTime.now(), LocalDateTime.now()),
                        new StationResponse(5L, "선릉역", LocalDateTime.now(), LocalDateTime.now())
                ),
                10,
                10
                )
        );
    }

}
