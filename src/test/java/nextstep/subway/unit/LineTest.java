package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    Line 신분당선;
    Station 신논현역;
    Station 논현역;
    Station 강남역;
    Station 양재역;
    Section 논현역_신논현역_구간;
    Section 신논현역_강남역_구간;
    Section 강남역_양재역_구간;

    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "RED");
        신논현역 = new Station("신논현역");
        논현역 = new Station("논현역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        논현역_신논현역_구간 = new Section(신분당선, 신논현역, 논현역, 5);
        신논현역_강남역_구간 = new Section(신분당선, 신논현역, 강남역, 5);
        강남역_양재역_구간 = new Section(신분당선, 강남역, 양재역, 5);
    }

    @DisplayName("구간 등록 성공")
    @Test
    void addSection() {
        신분당선.addSection(신논현역, 논현역, 5);
        assertThat(신분당선.sections().size()).isEqualTo(1);
        assertThat(신분당선.sections().stream().findFirst().orElseThrow().getUpStation().getName()).isEqualTo("신논현역");
        assertThat(신분당선.sections().stream().findFirst().orElseThrow().getDownStation().getName()).isEqualTo("논현역");
    }

    @DisplayName("역 목록 조회")
    @Test
    void getStations() {
        신분당선.addSection(논현역, 신논현역, 5);
        신분당선.addSection(강남역, 양재역, 5);
        assertThat(신분당선.stations()).contains(논현역);
        assertThat(신분당선.stations()).contains(신논현역);
        assertThat(신분당선.stations()).contains(강남역);
        assertThat(신분당선.stations()).contains(양재역);
    }

    @DisplayName("구간 삭제")
    @Test
    void removeSection() {
        신분당선.addSection(신논현역, 논현역, 5);
        신분당선.addSection(강남역, 양재역, 5);
        신분당선.deleteSection(신분당선.sections().stream().findFirst().orElseThrow());
        assertThat(신분당선.sections().size()).isEqualTo(1);
    }
}
