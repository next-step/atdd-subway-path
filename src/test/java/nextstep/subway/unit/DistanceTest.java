package nextstep.subway.unit;

import nextstep.subway.domain.Distance;
import nextstep.subway.exception.InvalidSectionDistanceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 구간 길이 단위 테스트")
class DistanceTest {

    @DisplayName("지하철 구간 길이를 생성한다.")
    @Test
    void create() {
        // given
        Distance distance = new Distance(5);

        // when & then
        assertThat(distance).isEqualTo(new Distance(5));
    }

    @DisplayName("지하철 구간 길이를 증가시킨다.")
    @Test
    void increase() {
        // given
        Distance distance = new Distance(5);

        // when
        distance.increase(5);

        // then
        assertThat(distance).isEqualTo(new Distance(10));
    }

    @DisplayName("지하철 구간의 길이를 감소시킨다.")
    @Test
    void decrease() {
        // given
        Distance distance = new Distance(5);

        // when
        distance.decrease(3);

        // then
        assertThat(distance).isEqualTo(new Distance(2));
    }

    @DisplayName("지하철 구간의 길이 증가 시, 0 또는 음수 값은 증가되지 않는다.")
    @ValueSource(ints = {0, -1})
    @ParameterizedTest
    void notIncreased(int value) {
        // given
        Distance distance = new Distance(5);

        // when
        distance.increase(value);

        // then
        assertThat(distance).isEqualTo(new Distance(5));
    }

    @DisplayName("지하철 구간의 길이가 1보다 작으면 예외가 발생한다.")
    @ValueSource(ints = {0, -1})
    @ParameterizedTest
    void invalidValue(int value) {
        // when & then
        assertThatThrownBy(() -> new Distance(value))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간의 길이 감소 시, 기존 값보다 크거나 같은 값은 감소할 수 없다.")
    @ValueSource(ints = {10, 11})
    @ParameterizedTest
    void invalidDecreasedValue(int value) {
        // given
        Distance distance = new Distance(10);

        // when & then
        assertThatThrownBy(() -> distance.decrease(value))
            .isInstanceOf(InvalidSectionDistanceException.class);
    }
}
