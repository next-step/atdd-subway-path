package nextstep.subway.line.domain;

import nextstep.subway.exceptions.BadRequestException;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {

    static final Long 이호선_아이디 = 1L;

    static final Long 이호선_상행종점역_아이디 = 1L;
    static final Long 이호선_하행종점역_아이디 = 2L;
    static final Long 새로운구간_상행역_아이디 = 3L;
    static final Long 새로운구간_하행역_아이디 = 4L;

    static final int 이호선_첫구간_길이 = 7;

    Line 이호선;
    Station 이호선_상행종점역;
    Station 이호선_하행종점역;

    @BeforeEach
    void setUp() {
        Line line = Line.builder()
                .id(이호선_아이디)
                .name("2호선")
                .color("bg-green")
                .build();

        Station 신도림역 = new Station(이호선_상행종점역_아이디, "신도림역");
        Station 영등포구청역 = new Station(이호선_하행종점역_아이디, "영등포구청역");

        line.addSection(신도림역, 영등포구청역, 이호선_첫구간_길이);

        이호선 = line;
        이호선_상행종점역 = 신도림역;
        이호선_하행종점역 = 영등포구청역;
    }

    @Test
    void 새로운구간_하행역이_노선의_하행종점역이_된다() {
        // given
        Station 구간_하행역 = new Station(새로운구간_하행역_아이디, "당산역");

        // when
        이호선.addSection(이호선_하행종점역, 구간_하행역, 5);

        // then
        assertThat(이호선.getAllStations()).containsExactly(이호선_상행종점역, 이호선_하행종점역, 구간_하행역);
    }

    @Test
    void 새로운구간_상행역이_노선의_상행종점역이_된다() {
        // given
        Station 구간_상행역 = new Station(새로운구간_상행역_아이디, "대림역");

        // when
        이호선.addSection(구간_상행역, 이호선_상행종점역, 5);

        // then
        assertThat(이호선.getAllStations()).containsExactly(구간_상행역, 이호선_상행종점역, 이호선_하행종점역);
    }

    @Test
    void 구간_추가() {
        // given
        Station 구간_하행역 = new Station(새로운구간_하행역_아이디, "문래역");

        // when
        이호선.addSection(이호선_상행종점역, 구간_하행역, 4);

        // then
        assertThat(이호선.getAllStations()).containsExactly(이호선_상행종점역, 구간_하행역, 이호선_하행종점역);
    }

    @Test
    void 새로운구간_상행역과_하행역이_모두_노선에_존재한다() {
        Assertions.assertThatThrownBy(() -> {
            이호선.addSection(이호선_상행종점역, 이호선_하행종점역, 4);
        }).isInstanceOf(BadRequestException.class)
                .hasMessage(Sections.BOTH_EXIST_EXCEPTION_MESSAGE);
    }

    @Test
    void 새로운구간_상행역과_하행역이_모두_노선에_존재하지_않는다(){
        // given
        Station 구간_상행역 = new Station(새로운구간_상행역_아이디, "당산역");
        Station 구간_하행역 = new Station(새로운구간_하행역_아이디, "합정역");

        // when, then
        Assertions.assertThatThrownBy(() ->
                        이호선.addSection(구간_상행역, 구간_하행역, 4))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(Sections.BOTH_NOT_EXIST_EXCEPTION_MESSAGE);
    }

    @Test
    void 새로운_구간의_길이가_기존_구간의_길이보다_작지않다(){
        // given
        Station 구간_하행역 = new Station(새로운구간_하행역_아이디, "문래역");

        // when, then
        Assertions.assertThatThrownBy(() ->
                        이호선.addSection(이호선_상행종점역, 구간_하행역, 이호선_첫구간_길이))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(Sections.DISTANCE_EXCEPTION_MESSAGE);
    }

}
