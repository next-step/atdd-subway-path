package nextstep.subway.path.domain;

import nextstep.subway.station.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

public class PathFinderHelper {
    public static Station 역_만들기(String name, Long id){
        Station station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);
        return station;
    }
}
