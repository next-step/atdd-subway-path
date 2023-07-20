package subway.unit.fixture;

import subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

import static utils.UnitTestUtils.createEntityTestIds;

public class StationSpec {
    private StationSpec() {
    }

    public static List<Station> of(List<String> stationNames) {
        final List<Station> stations = stationNames.stream()
                .map(Station::new)
                .collect(Collectors.toList());

        createEntityTestIds(stations, 1L);

        return stations;
    }

}
