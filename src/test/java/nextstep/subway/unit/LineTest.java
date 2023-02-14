package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    private Station 강남역;
    private Station 판교역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        판교역 = new Station("판교역");
        신분당선 = new Line("신분당선", "red");
    }

    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void addSection() {
        // when
        신분당선.addSection(강남역, 판교역, 3);

        // then
        assertThat(신분당선.getSections()).containsExactly(new Section(신분당선, 강남역, 판교역, 10));
    }

    @DisplayName("지하철 노선에 등록된 역들을 조회한다.")
    @Test
    void getStations() {
        // given
        신분당선.addSection(강남역, 판교역, 3);

        // when & then
        assertThat(신분당선.getStations()).containsExactly(강남역, 판교역);
    }

    @DisplayName("지하철 노선에 등록된 구간을 삭제한다.")
    @Test
    void removeSection() {
        // given
        신분당선.addSection(강남역, 판교역, 3);

        // when
        신분당선.removeSection(판교역);

        // then
        assertThat(신분당선.getStations()).isEmpty();
    }
}
