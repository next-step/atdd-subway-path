package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {

    private Line 신분당선;
    private Station 강남역;
    private Station 양재역;
    private Station 양재시민의숲역;

    @BeforeEach
    void setUp() {
        신분당선 = new Line();
        강남역 = new Station(1L, "강남역");
        양재역 = new Station(2L, "양재역");
        양재시민의숲역 = new Station(3L, "양재시민의숲역");
    }

    @Test
    void 노선에_구간을_추가한다() {
        // when
        신분당선.addSection(강남역, 양재역, 10);

        // then
        assertAll(() -> {
                    assertThat(신분당선.getSections()).hasSize(1);
                    assertThat(신분당선.getSections().get(0).getUpStation()).isEqualTo(강남역);
                    assertThat(신분당선.getSections().get(0).getDownStation()).isEqualTo(양재역);
                }
        );
    }

    @Test
    void 노선에서_역을_조회한다() {
        // given
        신분당선.addSection(강남역, 양재역, 10);
        신분당선.addSection(양재역, 양재시민의숲역, 10);

        // when
        var result = 신분당선.getStations();

        // then
        assertThat(result).containsExactly(강남역, 양재역, 양재시민의숲역);
    }

    @Test
    void 노선에서_구간을_삭제한다() {
        // given
        신분당선.addSection(강남역, 양재역, 10);
        신분당선.addSection(양재역, 양재시민의숲역, 10);

        // when
        신분당선.removeSection(양재시민의숲역);

        // then
        assertAll(() -> {
                    assertThat(신분당선.getSections()).hasSize(1);
                    assertThat(신분당선.getStations()).containsExactly(강남역, 양재역);
                }
        );
    }
}
