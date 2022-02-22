package nextstep.subway.unit;

import nextstep.subway.domain.Distance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {
    private Distance distanceOne = new Distance(1);
    private Distance distanceTwo = new Distance(2);

    @DisplayName("구간의 거리는 0보다 커야한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void createDistance(int value) {
        assertThatThrownBy(() -> new Distance(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간의 거리 더하기")
    @Test
    void plus() {
        assertThat(distanceOne.plus(distanceOne)).isEqualTo(distanceTwo);
    }

    @DisplayName("구간의 거리 빼기")
    @Test
    void minus() {
        assertThat(distanceTwo.minus(distanceOne)).isEqualTo(distanceOne);
    }
}
