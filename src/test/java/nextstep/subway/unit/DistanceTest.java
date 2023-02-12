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
        Distance distance = new Distance(5);
        assertThat(distance).isEqualTo(new Distance(5));
    }

    @DisplayName("지하철 구간의 길이가 1보다 작으면 예외가 발생한다.")
    @ValueSource(ints = {0, -1})
    @ParameterizedTest
    void invalidValue(int value) {
        assertThatThrownBy(() -> new Distance(value))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간의 길이 감소 시, 기존 값보다 크거나 같은 값은 감소할 수 없다.")
    @ValueSource(ints = {10, 11})
    @ParameterizedTest
    void invalidDecreasedValue(int value) {
        Distance distance = new Distance(10);
        assertThatThrownBy(() -> distance.decrease(value))
            .isInstanceOf(InvalidSectionDistanceException.class);
    }
}
