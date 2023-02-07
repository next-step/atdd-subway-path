package nextstep.subway.unit;

import nextstep.subway.common.exception.DistanceGreaterThanException;
import nextstep.subway.domain.Distance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static nextstep.subway.common.error.SubwayError.NO_REGISTER_DISTANCE_GREATER_THAN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 거리 객체 테스트")
class DistanceTest {

    @DisplayName("기존 구간 거리에서 입력 받은 거리를 뺀 나머지를 구한다.")
    @Test
    void minusDistance() {
        final Distance distance = new Distance(10);
        distance.minus(4);
        assertThat(distance).isEqualTo(new Distance(6));
    }

    @DisplayName("기존 구간 거리에서 입력 받은 거리가 크거나 같으면 익셉션 발생한다.")
    @ParameterizedTest(name = "{1}이 {0}보다 크거나 같기 때문에 익셉션 발생한다.")
    @CsvSource(value = {"10,10", "10,11"})
    void validateDistance(final Integer source, final Integer target) {
        final Distance distance = new Distance(source);
        assertThatThrownBy(() -> distance.minus(target))
                .isInstanceOf(DistanceGreaterThanException.class)
                .hasMessage(NO_REGISTER_DISTANCE_GREATER_THAN.getMessage());
    }
}