package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PairedStations;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.utils.EntityFixtures;

import java.util.HashMap;
import java.util.Map;

public class LineFixtures {
    public static final int END_STATIONS_DISTANCE = 9999;

    public static Line 상행종점역_하행종점역만_가진_구간이_포함된_노선_생성(Long upStationId, Long downStationId) {
        Station upStation = 역_생성(upStationId);
        Station downStation = 역_생성(downStationId);

        return new Line("2호선", "green", new PairedStations(upStation, downStation), END_STATIONS_DISTANCE);
    }

    public static Section 구간_생성(Long sectionId, Long upStationId, Long downStationId, int distance) {
        Station upStation = EntityFixtures.createEntityFixtureWithId(upStationId, Station.class);
        Station downStation = EntityFixtures.createEntityFixtureWithId(downStationId, Station.class);


        Map<String, Object> fieldSet = new HashMap<>();
        fieldSet.put("id", sectionId);
        fieldSet.put("upStation", upStation);
        fieldSet.put("downStation", downStation);
        fieldSet.put("distance", distance);

        return EntityFixtures.createEntityFixtureWithFieldSet(fieldSet, Section.class);
    }

    public static Station 역_생성(Long id) {
        return EntityFixtures.createEntityFixtureWithId(id, Station.class);
    }

}
