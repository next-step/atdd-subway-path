package nextstep.config.fixtures;

import nextstep.subway.dto.SectionRequest;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;

public class SectionFixture {
    public static Section 강남_양재_구간(int distance, Line line) {
        return new Section(StationFixture.강남, StationFixture.양재, distance, line);
    }

    public static Section 잘못된_강남_강남_구간(int distance, Line line) {
        return new Section(StationFixture.강남, StationFixture.강남, distance, line);
    }

    public static Section 삼성_선릉_구간(int distance, Line line) {
        return new Section(StationFixture.삼성, StationFixture.선릉, distance, line);
    }

    public static Section 선릉_역삼_구간(int distance, Line line) {
        return new Section(StationFixture.선릉, StationFixture.역삼, distance, line);
    }

    public static Section 역삼_강남_구간(int distance, Line line) {
        return new Section(StationFixture.역삼, StationFixture.강남, distance, line);
    }

    public static Section 강남_서초_구간(int distance, Line line) {
        return new Section(StationFixture.강남, StationFixture.서초, distance, line);
    }

    public static Section 역삼_삼성_구간(int distance, Line line) {
        return new Section(StationFixture.역삼, StationFixture.삼성, 10, line);
    }

    public static SectionRequest 지하철_구간(Long upStationId, Long downStationId, int distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }

}
