package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

class LineTest {

    private Line 이호선;

    private Section 강남역_역삼역;
    private Station 강남역;
    private Station 역삼역;

    @BeforeEach
    public void init() {
        이호선 = new Line("2호선", "green");

        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        강남역_역삼역 = new Section(강남역, 역삼역, 10);
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

        Section 선릉역_강남역 = new Section(선릉역, 강남역, 10);

        //then
        assertThatThrownBy(() -> 삼호선.addSection(선릉역_강남역))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(CustomException.DUPLICATE_STATION_MSG);
    }

    /**
     *                선릉역
     *                  |
     *  강남역 --- 역삼역 --- 삼성역
     */
    @Test
    @Deprecated
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
    void removeSection() {
        //given
        이호선.addSection(강남역_역삼역);

        //when
        이호선.removeSection(역삼역);

        //then
        assertThat(이호선.getStations()).doesNotContain(역삼역);
    }

    /**
     * 강남역(제거) --- 역삼역
     */
    @Test
    void removeSection_When_마지막_구간이_아니면_Then_ThrownException() {
        //given
        이호선.addSection(강남역_역삼역);

        //then
        assertThatThrownBy(() -> 이호선.removeSection(강남역))
                            .isInstanceOf(CustomException.class)
                            .hasMessageContaining(CustomException.ONLY_CAN_REMOVE_LAST_STATION_MSG);
    }
}
