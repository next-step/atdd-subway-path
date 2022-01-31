package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {
    private final Line 신분당선 = new Line("신분당선", "red");
    private final Station 강남역 = new Station("강남역");
    private final Station 정자역 = new Station("정자역");
    private final int distance = 10;

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection() {
        // given
        신분당선.addSection(강남역, 정자역, distance);
        Station 신논현역 = new Station("신논현역");

        // when
        신분당선.addSection(신논현역, 강남역, distance);

        // then
        assertThat(신분당선.getStations()).containsExactly(신논현역, 강남역, 정자역);
    }
}