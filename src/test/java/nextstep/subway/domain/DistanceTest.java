package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("거리 관련 기능")
class DistanceTest {

    @DisplayName("거리를 생성한다.")
    @Test
    void create() {
        Distance distance = new Distance(5);

        assertThat(distance).isEqualTo(new Distance(5));
    }

    @DisplayName("거리 감소시킨다.")
    @ParameterizedTest(name = "value : {0}")
    @ValueSource(ints = {1, 2, 3, 4})
    void minus(int value) {
        Distance actual = new Distance(5);

        assertThat(actual.minus(new Distance(value))).isEqualTo(new Distance(5 - value));
    }

    @DisplayName("거리 감소시 0이하가 되면 예외 처리한다.")
    @ParameterizedTest(name = "value : {0}")
    @ValueSource(ints = {5, 6, 100})
    void minusUnderZero(int value) {
        Distance distance = new Distance(5);

        assertThatThrownBy(() -> distance.minus(new Distance(value))).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("거리를 연장시킨다.")
    @ParameterizedTest(name = "value : {0}")
    @ValueSource(ints = {1, 2, 300, 5000})
    void plus(int value) {
        Distance distance = new Distance(5);

        assertThat(distance.plus(new Distance(value))).isEqualTo(new Distance(5 + value));
    }
}
