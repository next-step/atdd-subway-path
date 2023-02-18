package nextstep.subway.domain;

import nextstep.subway.domain.exceptions.NotPositiveNumberException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {

    @Test
    void 거리를_정상적으로_생성한다() {
        assertThat(Distance.of(3)).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void 거리가_음수이거나_0인_경우_예외가_발생한다(int value) {
        assertThatThrownBy(() -> Distance.of(value)).isInstanceOf(NotPositiveNumberException.class);
    }

    @Test
    void 거리에_거리를_뺄_수_있다() {
        var five = Distance.of(5);
        var three = Distance.of(3);
        var two = Distance.of(2);

        assertThat(five.minus(three)).isEqualTo(two);
    }

    @Test
    void 거리에_거리를_더할_수_있다() {
        var five = Distance.of(5);
        var three = Distance.of(3);
        var eight = Distance.of(8);

        assertThat(five.plus(three)).isEqualTo(eight);
    }
}
