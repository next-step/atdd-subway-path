package nextstep.subway.applicaion.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineRequestTest {

    @Test
    @DisplayName("upStationId, downStationId 값이 존재하고 distance 값이 0이 아니면 Section을 만들도록 True를 반환한다.")
    void isCreateSectionTest() {
        assertThat(new LineRequest("4호선", "blue").isCreateSection()).isFalse();
        assertThat(new LineRequest("4호선", "blue", 1L, 2L, 10).isCreateSection()).isTrue();
    }

    @Test
    @DisplayName("upStationId가 없으면 Section을 만들지 않도록 false를 반환한다.")
    void isCreateSectionWithoutUpStationIdTest() {
        assertThat(new LineRequest("4호선", "blue", null, 2L, 10).isCreateSection()).isFalse();
    }

    @Test
    @DisplayName("downStationId가 없으면 Section을 만들지 않도록 false를 반환한다.")
    void isCreateSectionWithoutDownStationIdTest() {
        assertThat(new LineRequest("4호선", "blue", 1L, null, 10).isCreateSection()).isFalse();
    }

    @Test
    @DisplayName("distance가 0이면 Section을 만들지 않도록 false를 반환한다.")
    void isCreateSectionWithoutDistanceZeroTest() {
        assertThat(new LineRequest("4호선", "blue", 1L, 2L, 0).isCreateSection()).isFalse();
    }
}