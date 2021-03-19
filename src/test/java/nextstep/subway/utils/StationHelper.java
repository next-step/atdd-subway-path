package nextstep.subway.utils;

import nextstep.subway.station.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

public class StationHelper {
  public static Station createStation(String name, Long id) {
    Station station = new Station(name);
    ReflectionTestUtils.setField(station, "id", id);
    return station;
  }
}
