package nextstep.config.fixtures;

import nextstep.subway.entity.Section;

public class SectionFixture {
    public static final Section 강남_양재_구간 =
            new Section(StationFixture.강남, StationFixture.양재, 10);

    public static final Section 잘못된_강남_강남_구간 =
            new Section(StationFixture.강남, StationFixture.강남, 10);

    public static final Section 삼성_선릉_구간 =
            new Section(StationFixture.삼성, StationFixture.선릉, 10);

    public static final Section 선릉_역삼_구간 =
            new Section(StationFixture.선릉, StationFixture.역삼, 10);
    public static final Section 역삼_삼성_구간 =
            new Section(StationFixture.역삼, StationFixture.삼성, 10);;
}
