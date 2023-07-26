package nextstep.subway.unit.fake;

import nextstep.subway.station.repository.Station;
import nextstep.subway.station.service.StationFindable;

import java.util.List;

import static nextstep.subway.unit.fixture.StationFixture.*;

public class FakeStationFindable implements StationFindable {
    @Override
    public Station findStationById(Long stationId) {
        if (stationId == 강남역_ID) {
            return 강남역();
        }
        if (stationId == 신논현역_ID) {
            return 신논현역();
        }
        if (stationId == 논현역_ID) {
            return 논현역();
        }
        return null;
    }

    @Override
    public List<Station> findAllStations() {
        return null;
    }
}
