package nextstep.subway.unit;

import nextstep.subway.common.Constant;
import nextstep.subway.exception.AlreadyExistDownStationException;
import nextstep.subway.exception.IsNotLastStationException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LineTest {

    Line 신분당선 = Line.of(Constant.신분당선, Constant.빨간색);
    Station 신논현역 = Station.from(Constant.신논현역);
    Station 강남역 = Station.from(Constant.강남역);
    Station 양재역 = Station.from(Constant.양재역);

    @BeforeEach
    protected void setUp() {
    }

    @Test
    @DisplayName("노선에 구간을 등록")
    void 노선에_구간을_등록() {
        // given
        Section 신논현역_강남역_구간 = Section.of(신논현역, 강남역, Constant.기본_역_간격);

        // when
        신분당선.addSection(신논현역_강남역_구간);

        // then
        assertThat(신분당선.hasSection(신논현역_강남역_구간)).isTrue();
    }

    @Test
    @DisplayName("노선의 하행 종점역이 아닌 상행역을 가진 지하철 구간을 등록하면 예외 발생")
    void 노선의_하행_종점역이_아닌_상행역을_가진_지하철_구간을_등록하면_예외_발생() {
        // given
        Section 신논현역_강남역_구간 = Section.of(신논현역, 강남역, Constant.기본_역_간격);
        Section 신논현역_양재역_구간 = Section.of(신논현역, 양재역, Constant.기본_역_간격);
        신분당선.addSection(신논현역_강남역_구간);

        // when & then
        assertThatThrownBy(() -> 신분당선.addSection(신논현역_양재역_구간))
                .isInstanceOf(IsNotLastStationException.class);
    }

    @Test
    @DisplayName("등록할 역이 이미 있는 지하철 노선에 구간을 등록하면 예외 발생")
    void 등록할_역이_이미_있는_지하철_노선에_구간을_등록하면_예외_발생() {
        // given
        Section 신논현역_강남역_구간 = Section.of(신논현역, 강남역, Constant.기본_역_간격);
        Section 강남역_신논현역_구간 = Section.of(강남역, 신논현역, Constant.기본_역_간격);
        신분당선.addSection(신논현역_강남역_구간);

        // when & then
        assertThatThrownBy(() -> 신분당선.addSection(강남역_신논현역_구간))
                .isInstanceOf(AlreadyExistDownStationException.class);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
