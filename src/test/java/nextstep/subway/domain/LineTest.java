package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    private Line 신분당선;
    private Station 강남역;
    private Station 판교역;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        판교역 = new Station("판교역");
        신분당선 = new Line("신분당선", "green");

        신분당선.addSection(강남역, 판교역, 10);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        assertThat(신분당선.getStations().size()).isEqualTo(2);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        assertThat(신분당선.getStations()).containsExactly(강남역, 판교역);
    }

    @DisplayName("노선 업데이트")
    @Test
    void update() {
        신분당선.update("뉴 신분당선", "new color");
        assertThat(신분당선.getName()).isEqualTo("뉴 신분당선");
        assertThat(신분당선.getColor()).isEqualTo("new color");
    }
}
