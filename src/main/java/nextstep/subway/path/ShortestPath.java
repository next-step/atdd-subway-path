package nextstep.subway.path;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import nextstep.subway.station.domain.Station;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShortestPath {
    private List<Station> stations;
    private int distance;

}
