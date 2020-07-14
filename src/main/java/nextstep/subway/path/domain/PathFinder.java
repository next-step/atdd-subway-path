package nextstep.subway.path.domain;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PathFinder {

    public ShortestPathResult findShortestPath(List<LineResponse> lines, Long startStationId, Long endStationId, PathFindType type) {
        return ShortestPathResult.empty();
    }

    @Bean
    public PathFinder pathFinder() {
        return new PathFinder();
    }
}
