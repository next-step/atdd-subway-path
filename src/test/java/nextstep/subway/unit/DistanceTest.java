package nextstep.subway.unit;

import nextstep.subway.domain.Distance;
import nextstep.subway.exception.InvalidSectionDistanceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 구간 길이 단위 테스트")
class DistanceTest {

    @DisplayName("지하철 구간의 길이가 1보다 작으면 예외가 발생한다.")
    @ValueSource(ints = {0, -1})
    @ParameterizedTest
    void invalidValue(int value) {
        assertThatThrownBy(() -> new Distance(value))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("새로운 지하철 구간의 추가에 의한 기존 지하철 구간의 길이가 감소할 때, 기존 구간의 길이는 1보다 작아질 수 없다.")
    @Test
    void invalidDecreasedValue() {
        Distance distance = new Distance(10);
        assertThatThrownBy(() -> distance.decrease(10))
            .isInstanceOf(InvalidSectionDistanceException.class);
    }
}
