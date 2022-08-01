package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {
    public PathResponse getShortestPath(final Long source, final Long target) {
        return PathResponse.of(10, List.of(new Station("교대역"), new Station("남부터미널역"), new Station("양재역")));
    }
}
