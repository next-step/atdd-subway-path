package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    private Line 이호선;

    private Section 강남역_역삼역;
    private Section 역삼역_선릉역;

    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;

    @BeforeEach
    public void init() {
        이호선 = new Line("2호선", "green");

        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");

        강남역_역삼역 = new Section(강남역, 역삼역, 10);
        역삼역_선릉역 = new Section(역삼역, 선릉역, 15);
    }

    @Test
    void addSection() {
        //when
        이호선.addSection(강남역_역삼역);

        //then
        assertThat(이호선.getSections()).containsExactlyElementsOf(List.of(강남역_역삼역));
    }

    /**
     *  강남역 --- 역삼역 --- 선릉역
     *                      |
     *                     선릉역  --- 강남역(중복)
     */
    @Test
    void addSection_When_중복된역을_추가하면_Then_ThrownException() {
        //given
        Station 선릉역 = new Station(3L, "선릉역");
        Section 역삼억_선릉역 = new Section(역삼역, 선릉역, 10);
        Line 삼호선 = new Line("삼호선", "blue", List.of(강남역_역삼역, 역삼억_선릉역));

        Section 강남역_선릉역 = new Section(강남역, 선릉역, 10);

        //then
        assertThatThrownBy(() -> 삼호선.addSection(강남역_선릉역))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(CustomException.DUPLICATE_STATION_MSG);
    }

    @Test
    void addSection_When_새로운_구간의_길이가_기존_길이보다_길면_Then_ThrownException() {
        //given
        Station 선릉역 = new Station(3L, "선릉역");
        Section 강남억_선릉역 = new Section(강남역, 선릉역, 10);
        Line 삼호선 = new Line("삼호선", "blue", List.of(강남억_선릉역));

        Section 강남역_역삼역 = new Section(강남역, 역삼역, 20);

        //then
        assertThatThrownBy(() -> 삼호선.addSection(강남역_역삼역))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(CustomException.CAN_NOT_ADD_SECTION_CAUSE_DISTANCE);
    }

    @Test
    void addSection_When_새로운_구간의_역이_기존_노선에_존재하지_않는다면_Then_ThrownException() {
        //given
        Station 선릉역 = new Station(3L, "선릉역");
        Station 삼성역 = new Station(4L, "삼성역");
        Section 강남억_역삼역 = new Section(강남역, 역삼역, 10);
        Line 삼호선 = new Line("삼호선", "blue", List.of(강남억_역삼역));

        Section 선릉역_삼성역 = new Section(선릉역, 삼성역, 20);

        //then
        assertThatThrownBy(() -> 삼호선.addSection(선릉역_삼성역))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(CustomException.ADD_STATION_MUST_INCLUDE_IN_LINE);
    }

    /**
     * 요구사항 변경으로 disabled
     *                선릉역
     *                  |
     *  강남역 --- 역삼역 --- 삼성역
     */
    @Test
    @Disabled
    void addSection_When_중간역에_추가하면_Then_ThrownException() {
        //given
        Station 선릉역 = new Station(3L, "선릉역");
        Station 삼성역 = new Station(4L, "선릉역");
        Section 역삼역_삼성역 = new Section(역삼역, 삼성역, 10);
        Section 선릉역_삼성역 = new Section(선릉역, 삼성역, 10);

        Line 삼호선 = new Line("삼호선", "blue", List.of(강남역_역삼역, 역삼역_삼성역));

        //then
        assertThatThrownBy(() -> 삼호선.addSection(선릉역_삼성역))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(CustomException.ONLY_CAN_CREATE_LAST_STATION_MSG);
    }


    @Test
    void getStations() {
        //given
        이호선.addSection(강남역_역삼역);

        //when
        List<Station> stations = 이호선.getStations();

        //then
        assertThat(stations).containsExactlyElementsOf(List.of(강남역, 역삼역));
    }

    @Test
    @DisplayName("마지막 구간을 제거한다")
    void removeSection_When_마지막_역을_제거하면_Then_마지막_구간제거() {
        //given
        이호선.addSection(강남역_역삼역);
        이호선.addSection(역삼역_선릉역);

        //when
        이호선.removeSection(선릉역);

        //then
        assertThat(이호선.getStations()).doesNotContain(선릉역);
    }

    @Test
    @DisplayName("중간 구간을 제거한다")
    void removeSection_When_중간_역을_제거하면_Then_중간_구간제거() {
        //given
        이호선.addSection(강남역_역삼역);
        이호선.addSection(역삼역_선릉역);

        //when
        이호선.removeSection(역삼역);

        //then
        assertThat(이호선.getStations()).containsExactlyElementsOf(List.of(강남역, 선릉역));
        assertThat(이호선.getSections().get(0).getDistance()).isEqualTo(25);
    }

    @Test
    void removeSection_Given_노선에_구간이_하나뿐일때_When_구간을_제거하면_Then_ThrowException() {
        //given
        이호선.addSection(강남역_역삼역);

        //then
        assertThatThrownBy(() -> 이호선.removeSection(역삼역))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(CustomException.LINE_HAS_SECTION_AT_LEAST_ONE);
    }

    /**
     * 요구사항 변경으로 disabled
     *
     * 강남역(제거) --- 역삼역
     */
    @Test
    @Disabled
    void removeSection_When_마지막_구간이_아니면_Then_ThrownException() {
        //given
        이호선.addSection(강남역_역삼역);

        //then
        assertThatThrownBy(() -> 이호선.removeSection(강남역))
                            .isInstanceOf(CustomException.class)
                            .hasMessageContaining(CustomException.ONLY_CAN_REMOVE_LAST_STATION_MSG);
    }
}
