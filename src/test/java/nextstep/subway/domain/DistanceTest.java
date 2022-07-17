package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {

    @Test
    @DisplayName("길이가 1보다 작을 경우 객체 생성 시 예외를 반환한다.")
    void create_invalid_distance() {
        //given
        final int distance = -1;

        // when
        assertThatThrownBy(() -> new Distance(distance))
                .isInstanceOf(IllegalArgumentException.class);
    }
}