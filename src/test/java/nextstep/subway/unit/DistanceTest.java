package nextstep.subway.unit;

import nextstep.subway.domain.Distance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class DistanceTest {

    @Test
    void 거리를_생성한다() {
        // given
        Distance distance = new Distance(1);

        // then
        assertThat(distance.getDistance()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -2})
    void 거리가_1미만인_경우_예외를_일으킨다(int input) {
        assertThatIllegalArgumentException().isThrownBy(() ->
                new Distance(input)
        );
    }
}
