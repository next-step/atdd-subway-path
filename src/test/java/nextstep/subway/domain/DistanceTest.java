package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    @DisplayName("거리를 줄일 때 자신의 거리보다 작지 않으면 예외를 반환한다.")
    void invalidReduce() {
        // given
        Distance distance = new Distance(10);

        // when
        Distance ten = new Distance(10);

        // then
        assertThatThrownBy(() -> distance.reduce(ten))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("거리를 줄이면 해당 값만큼 거리가 줄어든다.")
    void reduce() {
        // given
        Distance distance = new Distance(10);

        // when
        Distance five = new Distance(5);
        Distance reducedDistance = distance.reduce(five);

        // then
        assertThat(reducedDistance).isEqualTo(new Distance(5));
    }
}