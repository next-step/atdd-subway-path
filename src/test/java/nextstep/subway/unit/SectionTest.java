package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.enums.SubwayErrorMessage;
import nextstep.subway.fake.FakeLineFactory;
import nextstep.subway.fake.FakeStationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest{

    protected Line 분당선;
    protected Section 선릉_영통_거리10_구간;
    protected Section 영통_구의_거리10_구간;
    protected Section 영통_신촌_거리7_구간;
    protected Section 신촌_영통_거리7_구간;


    @BeforeEach
    void setUp() {
        분당선 = FakeLineFactory.분당선();
        선릉_영통_거리10_구간 = new Section(분당선, FakeStationFactory.선릉역(), FakeStationFactory.영통역(), 10);
        영통_구의_거리10_구간 = new Section(분당선, FakeStationFactory.영통역(), FakeStationFactory.구의역(), 10);
        영통_신촌_거리7_구간 = new Section(분당선, FakeStationFactory.영통역(), FakeStationFactory.신촌역(), 7);
        신촌_영통_거리7_구간 = new Section(분당선, FakeStationFactory.신촌역(), FakeStationFactory.영통역(), 7);
    }

    @Test
    @DisplayName("기존 상행역을 새로운 구간의 하행역으로 변경한다")
    void changeUpStation() {
        Sections sections = 분당선.getSections();
        sections.add(영통_구의_거리10_구간);

        영통_구의_거리10_구간.changeSectionConditionBy(영통_신촌_거리7_구간);

        //then
        assertThat(영통_구의_거리10_구간.getUpStation()).isEqualTo(영통_신촌_거리7_구간.getDownStation());
    }

    @Test
    @DisplayName("기존 하행역을 새로운 구간의 상행역으로 변경한다")
    void changeDownStation() {
        Sections sections = 분당선.getSections();
        sections.add(선릉_영통_거리10_구간);

        선릉_영통_거리10_구간.changeSectionConditionBy(신촌_영통_거리7_구간);

        //then
        assertThat(선릉_영통_거리10_구간.getDownStation()).isEqualTo(신촌_영통_거리7_구간.getUpStation());
    }

    @Test
    @DisplayName("구간 등록 실패 테스트 - 기존 구간보다 거리가 긴 구간을 등록한다")
    void sectionChangeFailedTest() {
        //given
        Sections sections = 분당선.getSections();
        sections.add(영통_신촌_거리7_구간);

        //then
        assertThatThrownBy(
                () -> 영통_신촌_거리7_구간.changeSectionConditionBy(영통_구의_거리10_구간)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage(SubwayErrorMessage.INVALID_DISTANCE.getMessage());
    }


}
