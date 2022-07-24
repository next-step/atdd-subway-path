package nextstep.subway.utils;

import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import org.springframework.test.util.ReflectionTestUtils;

public final class LineTestFixtures {

    public static Line 노선_생성(final String 노선이름, final String 노선색) {
        return Line.makeLine(노선이름, 노선색);
    }

    public static SectionRequest 구간요청_생성(long upStationId, long downStationId, int distance) {
        SectionRequest sectionRequest = new SectionRequest();
        ReflectionTestUtils.setField(sectionRequest, "upStationId", upStationId);
        ReflectionTestUtils.setField(sectionRequest, "downStationId", downStationId);
        ReflectionTestUtils.setField(sectionRequest, "distance", distance);
        return sectionRequest;
    }
}
