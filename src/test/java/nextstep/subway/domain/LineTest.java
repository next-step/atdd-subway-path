package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Station 강남역;
    private Station 판교역;
    private Station 정자역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = Station.of("강남역");
        판교역 = Station.of("판교역");
        정자역 = Station.of("정자역");
        신분당선 = Line.of("신분당선", "red");
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우 - 첫 구간 추가")
    @Test
    void addSection() {
        // when
        신분당선.addSection(강남역, 판교역, 10);

        // then
        assertThat(신분당선.getStations().size()).isEqualTo(2);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우 - 두 번째 구간 추가")
    @Test
    void addSection_towSection() {
        // when
        신분당선.addSection(강남역, 판교역, 10);
        신분당선.addSection(판교역, 정자역, 20);

        // then
        assertThat(신분당선.getStations().size()).isEqualTo(3);
    }


    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // when
        신분당선.addSection(강남역, 판교역, 10);
        신분당선.addSection(판교역, 정자역, 20);

        // then
        assertThat(신분당선.getStations()).contains(강남역, 판교역, 정자역);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void deleteStation() {
        // when
        신분당선.addSection(강남역, 판교역, 10);
        신분당선.addSection(판교역, 정자역, 20);

        // then
        assertThat(신분당선.getStations()).contains(강남역, 판교역);
    }

}