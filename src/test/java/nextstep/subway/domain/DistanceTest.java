package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {

    @Test
    @DisplayName("Distance 객체는 캐싱되어 있어 동일한 객체이다.")
    void cache_distance() {
        // when
        Distance one = Distance.valueOf(1);
        Distance sameOne = Distance.valueOf(1);

        // then
        assertThat(one == sameOne).isTrue();
    }

    @Test
    @DisplayName("길이가 1보다 작을 경우 객체 생성 시 예외를 반환한다.")
    void create_invalid_distance() {
        //given
        final int distance = -1;

        // when
        assertThatThrownBy(() -> Distance.valueOf(distance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("거리를 줄일 때 자신의 거리보다 작지 않으면 예외를 반환한다.")
    void invalidReduce() {
        // given
        Distance distance = Distance.valueOf(10);

        // when
        Distance ten = Distance.valueOf(10);

        // then
        assertThatThrownBy(() -> distance.reduce(ten))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("거리를 줄이면 해당 값만큼 거리가 줄어든다.")
    void reduce() {
        // given
        Distance distance = Distance.valueOf(10);

        // when
        Distance five = Distance.valueOf(5);
        Distance reducedDistance = distance.reduce(five);

        // then
        assertThat(reducedDistance).isEqualTo(Distance.valueOf(5));
    }
}