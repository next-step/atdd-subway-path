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

    public static Line 상행종점역부터_하행종점역까지_모든구간이_포함된_노선_생성(String lineName, Section... sections) {
        int sectionsSize = sections.length;
        if (sectionsSize == 0) {
            throw new IllegalArgumentException("노선 초기 생성시 1개 이상의 구간이 필요합니다.");
        }

        Line 노선 = new Line("2호선", "green", new PairedStations(sections[0].getUpStation(), sections[0].getDownStation()), sections[0].getDistance());

        for (int i = 1; i < sectionsSize; i++) {
            Section section = sections[i];
            노선.addSection(new PairedStations(section.getUpStation(), section.getDownStation()), section.getDistance());
        }

        return 노선;
    }

    public static Line 상행종점역_하행종점역만_가진_구간이_포함된_노선_생성(Station upStation, Station downStation) {
        return new Line("2호선", "green", new PairedStations(upStation, downStation), END_STATIONS_DISTANCE);
    }

    public static Line 상행종점역_하행종점역만_가진_구간이_포함된_노선_생성(Long upStationId, Long downStationId) {
        Station upStation = 역_생성(upStationId);
        Station downStation = 역_생성(downStationId);
        return 상행종점역_하행종점역만_가진_구간이_포함된_노선_생성(upStation, downStation);
    }

    public static Section 구간_생성(Long sectionId, Station upStation, Station downStation, int distance) {
        return 구간_생성(sectionId, upStation.getId(), downStation.getId(), distance);
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
