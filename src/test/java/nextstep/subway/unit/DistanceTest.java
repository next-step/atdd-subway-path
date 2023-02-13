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

    @DisplayName("지하철 구간의 길이가 1보다 작으면 예외가 발생한다.")
    @ValueSource(ints = {0, -1})
    @ParameterizedTest
    void invalidValue(int value) {
        // when & then
        assertThatThrownBy(() -> new Distance(value))
            .isInstanceOf(InvalidSectionDistanceException.class);
    }
}
