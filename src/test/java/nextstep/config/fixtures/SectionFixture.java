package nextstep.config.fixtures;

import nextstep.subway.entity.Section;

public class SectionFixture {
    public static final Section 강남_양재_구간 =
            new Section(StationFixture.강남, StationFixture.양재, 10);

    public static final Section 삼성_선릉_구간 =
            new Section(StationFixture.삼성, StationFixture.선릉, 10);
}
