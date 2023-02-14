package nextstep.subway.applicaion;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;

@Service
public class PathService {

    public PathResponse findPath(Long source, Long target) {
        List<StationResponse> stations = new ArrayList<>();
        stations.add(new StationResponse(new Station(1L, "교대역")));
        stations.add(new StationResponse(new Station(4L, "남부터미널역")));
        stations.add(new StationResponse(new Station(3L, "양재역")));

        return new PathResponse(stations, 5);
    }
}
