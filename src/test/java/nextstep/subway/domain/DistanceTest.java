package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.domain.exception.DistanceSizeException;
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

        assertThatThrownBy(() -> distance.minus(new Distance(value))).isInstanceOf(DistanceSizeException.class);
    }

    @DisplayName("거리를 연장시킨다.")
    @ParameterizedTest(name = "value : {0}")
    @ValueSource(ints = {1, 2, 300, 5000})
    void plus(int value) {
        Distance distance = new Distance(5);

        assertThat(distance.plus(new Distance(value))).isEqualTo(new Distance(5 + value));
    }

    @DisplayName("구간이 전달된 구간 이상인지 반환한다.")
    @Test
    void more() {
        Distance distance = new Distance(4);

        assertAll(
                () -> assertThat(distance.more(new Distance(3))).isTrue(),
                () -> assertThat(distance.more(new Distance(4))).isTrue(),
                () -> assertThat(distance.more(new Distance(6))).isFalse()
        );
    }

    @DisplayName("구간이 최솟값 미만인지 반환한다.")
    @Test
    void isLessMin() {
        assertAll(
                () -> assertThat(new Distance(1).isUnderMin()).isFalse(),
                () -> assertThatThrownBy(() -> new Distance(0)).isInstanceOf(DistanceSizeException.class),
                () -> assertThatThrownBy(() -> new Distance(-1)).isInstanceOf(DistanceSizeException.class)
        );
    }

    @DisplayName("거리 값을 반환한다.")
    @ParameterizedTest(name = "value : {0}")
    @ValueSource(ints = {1, 4, 100, 1200})
    void value(int value) {
        assertThat(new Distance(value).value()).isEqualTo(value);
    }
}
