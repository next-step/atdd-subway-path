package nextstep.subway.acceptance.fixture;

import java.util.HashMap;
import java.util.Map;

public class StationFixture {
    public static Map<String, String> createStationParams(String name) {
        Map<String, String> station = new HashMap<>();
        station.put("name", name);
        return station;
    }
}
