package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.fake.FakeLineFactory;
import nextstep.subway.fake.FakeStationFactory;
import org.junit.jupiter.api.BeforeEach;

public class SectionUnitTest {

    protected Line 분당선;
    protected Section 선릉_영통_거리10_구간;
    protected Section 영통_구의_거리10_구간;
    protected Section 강남_선릉_거리7_구간;
    protected Section 영통_신촌_거리7_구간;
    protected Section 신촌_영통_거리7_구간;
    protected Section 영통_강남_거리7_구간;
    protected Section 역삼_신촌_거리5_구간;


    @BeforeEach
    void setUp() {
        분당선 = FakeLineFactory.분당선();
        선릉_영통_거리10_구간 = new Section(분당선, FakeStationFactory.선릉역(), FakeStationFactory.영통역(), 10);
        영통_구의_거리10_구간 = new Section(분당선, FakeStationFactory.영통역(), FakeStationFactory.구의역(), 10);
        영통_강남_거리7_구간 = new Section(분당선, FakeStationFactory.영통역(), FakeStationFactory.강남역(), 7);
        강남_선릉_거리7_구간 = new Section(분당선, FakeStationFactory.강남역(), FakeStationFactory.선릉역(), 10);
        영통_신촌_거리7_구간 = new Section(분당선, FakeStationFactory.영통역(), FakeStationFactory.신촌역(), 7);
        신촌_영통_거리7_구간 = new Section(분당선, FakeStationFactory.신촌역(), FakeStationFactory.영통역(), 7);
        역삼_신촌_거리5_구간 = new Section(분당선, FakeStationFactory.역삼역(), FakeStationFactory.신촌역(), 5);
    }
}
