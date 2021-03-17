package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

public class LineHelper {

    public static Station 역_만들기(String stationName, Long id){
        Station station = new Station(stationName);
        ReflectionTestUtils.setField(station, "id", id);
        return station;
    }
}
