package nextstep.subway.unit;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

public final class LineUnitTestHelper {

    public static LineRequest 이호선_요청_객체_만들기(Station upStation, Station downStation) {
        LineRequest lineRequest = new LineRequest();
        ReflectionTestUtils.setField(lineRequest, "name", "2호선");
        ReflectionTestUtils.setField(lineRequest, "color", "bg-green-600");
        ReflectionTestUtils.setField(lineRequest, "upStationId", upStation.getId());
        ReflectionTestUtils.setField(lineRequest, "downStationId", downStation.getId());
        ReflectionTestUtils.setField(lineRequest, "distance", 5);
        return lineRequest;
    }

    public static LineRequest 신분당선_요청_객체_만들기(Station upStation, Station downStation) {
        LineRequest lineRequest = new LineRequest();
        ReflectionTestUtils.setField(lineRequest, "name", "신분당선");
        ReflectionTestUtils.setField(lineRequest, "color", "bg-red-600");
        ReflectionTestUtils.setField(lineRequest, "upStationId", upStation.getId());
        ReflectionTestUtils.setField(lineRequest, "downStationId", downStation.getId());
        ReflectionTestUtils.setField(lineRequest, "distance", 5);
        return lineRequest;
    }

    public static SectionRequest 교대_TO_역삼_구간_만들기(Station upStation, Station downStation) {
        SectionRequest sectionRequest = new SectionRequest();
        ReflectionTestUtils.setField(sectionRequest, "upStationId", upStation.getId());
        ReflectionTestUtils.setField(sectionRequest, "downStationId", downStation.getId());
        ReflectionTestUtils.setField(sectionRequest, "distance", 5);
        return sectionRequest;
    }

    public static SectionRequest 강남_TO_역삼_구간_만들기(Station upStation, Station downStation) {
        SectionRequest sectionRequest = new SectionRequest();
        ReflectionTestUtils.setField(sectionRequest, "upStationId", upStation.getId());
        ReflectionTestUtils.setField(sectionRequest, "downStationId", downStation.getId());
        ReflectionTestUtils.setField(sectionRequest, "distance", 2);
        return sectionRequest;
    }
}
