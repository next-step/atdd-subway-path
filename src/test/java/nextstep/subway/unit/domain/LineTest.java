package nextstep.subway.unit.domain;

import nextstep.subway.common.Constant;
import nextstep.subway.exception.DeleteSectionException;
import nextstep.subway.exception.IsNotLastStationException;
import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {

    Line 신분당선;
    Station 논현역;
    Station 신논현역;
    Station 강남역;
    Station 양재역;

    @BeforeEach
    protected void setUp() {
        신분당선 = Line.of(Constant.신분당선, Constant.빨간색);
        논현역 = Station.from(Constant.논현역);
        신논현역 = Station.from(Constant.신논현역);
        강남역 = Station.from(Constant.강남역);
        양재역 = Station.from(Constant.양재역);
    }

    @DisplayName("노선에 구간을 등록")
    @Test
    void 노선에_구간을_등록() {
        // given
        Section 신논현역_강남역_구간 = Section.of(신논현역, 강남역, Constant.역_간격_10);

        // when
        신분당선.addSection(신논현역_강남역_구간);

        // then
        assertThat(신분당선.hasSection(신논현역_강남역_구간)).isTrue();
    }

    @DisplayName("등록한 역을 조회")
    @Test
    void 등록한_역을_조회() {
        // given
        Section 신논현역_강남역_구간 = Section.of(신논현역, 강남역, Constant.역_간격_10);
        Section 강남역_양재역_구간 = Section.of(강남역, 양재역, Constant.역_간격_10);
        신분당선.addSection(신논현역_강남역_구간);
        신분당선.addSection(강남역_양재역_구간);

        // when
        List<Station> 신분당선_역_목록 = 신분당선.getStations();

        // then
        assertAll(
                () -> assertThat(신분당선_역_목록).contains(신논현역, 강남역, 양재역),
                () -> assertThat(신분당선_역_목록).doesNotContain(논현역)
        );
    }

    @DisplayName("구간을 삭제")
    @Test
    void 구간을_삭제() {
        // given
        Section 신논현역_강남역_구간 = Section.of(신논현역, 강남역, Constant.역_간격_10);
        Section 강남역_양재역_구간 = Section.of(강남역, 양재역, Constant.역_간격_10);
        신분당선.addSection(신논현역_강남역_구간);
        신분당선.addSection(강남역_양재역_구간);

        // when
        신분당선.deleteSection(양재역);

        // then
        assertAll(
                () -> assertThat(신분당선.hasSection(신논현역_강남역_구간)).isTrue(),
                () -> assertThat(신분당선.hasSection(강남역_양재역_구간)).isFalse()
        );
    }

    @DisplayName("노선에 등록되지 않은 역을 삭제하면 예외 발생")
    @Test
    void 노선에_등록되지_않은_역을_삭제하면_예외_발생() {
        // given
        Section 신논현역_강남역_구간 = Section.of(신논현역, 강남역, Constant.역_간격_10);
        Section 강남역_양재역_구간 = Section.of(강남역, 양재역, Constant.역_간격_10);
        신분당선.addSection(신논현역_강남역_구간);
        신분당선.addSection(강남역_양재역_구간);

        // when & then
        assertThatThrownBy(() -> 신분당선.deleteSection(논현역))
                .isInstanceOf(NotFoundStationException.class);
    }

    @DisplayName("남은 구간이 한개인 노선의 역을 삭제하면 예외 발생")
    @Test
    void 남은_구간이_한개인_노선의_역을_삭제하면_예외_발생() {
        // given
        Section 신논현역_강남역_구간 = Section.of(신논현역, 강남역, Constant.역_간격_10);
        신분당선.addSection(신논현역_강남역_구간);

        // when & then
        assertThatThrownBy(() -> 신분당선.deleteSection(강남역))
                .isInstanceOf(DeleteSectionException.class);
    }

}
