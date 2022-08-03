package nextstep.subway.utils;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

public final class StationTestFixtures {

    public static Station 지하철역_생성(final String 지하철역이름) {
        return new Station(지하철역이름);
    }

    public static Station 지하철역_생성_WITH_ID(final String 지하철역이름, final Long 지하철역_아이디) {
        final Station 지하철역 = 지하철역_생성("강남역");
        ReflectionTestUtils.setField(지하철역, "id", 지하철역_아이디);
        return 지하철역;
    }
}
