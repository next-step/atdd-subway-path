package nextstep.subway.applicaion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nextstep.subway.domain.Station;

import java.util.List;

@AllArgsConstructor
@Getter
public class ShortestPathResult {
    private final int distance;
    private final List<Station> stations;
}
