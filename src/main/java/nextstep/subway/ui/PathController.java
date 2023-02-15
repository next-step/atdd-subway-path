package nextstep.subway.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/paths")
public class PathController {
    private final LineService lineService;

    @GetMapping
    public PathResponse getPath() {

        return new PathResponse(List.of(new StationResponse(1L, "강남역"), new StationResponse(2L, "양재역")
                , new StationResponse(3L, "정자역")), 20);
    }

}
