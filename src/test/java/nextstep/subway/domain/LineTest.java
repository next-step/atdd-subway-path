package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Station 강남역;
    private Station 판교역;
    private Station 정자역;
    private Station 미금역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = Station.of("강남역");
        판교역 = Station.of("판교역");
        정자역 = Station.of("정자역");
        미금역 = Station.of("미금역");
        신분당선 = Line.of("신분당선", "red", 강남역, 정자역, 100);
    }

    @DisplayName("노선의 이름과 색을 변경할 수 있다")
    @Test
    void update() {
        // when
        String name = "구분당선";
        String color = "blue";
        신분당선.update(name, color);

        // then
        assertThat(신분당선.getName()).isEqualTo(name);
        assertThat(신분당선.getColor()).isEqualTo(color);
    }

    @DisplayName("노선에 구간을 추가한다")
    @Test
    void addSection_lastStation() {
        // when
        신분당선.addSection(정자역, 미금역, 10);

        // then
        assertThat(신분당선.getStations()).containsExactly(강남역, 정자역, 미금역);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // when
        신분당선.addSection(강남역, 판교역, 10);

        // then
        assertThat(신분당선.getStations()).contains(강남역, 판교역, 정자역);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void deleteStation() {
        // given
        신분당선.addSection(강남역, 판교역, 10);

        // when
        신분당선.deleteStation(정자역);

        // then
        assertThat(신분당선.getStations()).contains(강남역, 판교역);
    }

}