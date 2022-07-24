package nextstep.subway.utils;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public final class StationTestFixtures {

    public static Station 지하철역_생성(final String 지하철역이름) {
        return new Station(지하철역이름);
    }

}
