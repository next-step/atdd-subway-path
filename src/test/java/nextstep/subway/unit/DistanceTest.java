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

    @Test
    void 거리를_뺀다() {
        // given
        Distance distance1 = new Distance(2);
        Distance distance2 = new Distance(1);

        // when
        Distance newDistance = distance1.minus(distance2);

        // then
        assertThat(newDistance).isEqualTo(new Distance(1));
    }

    @Test
    void 거리를_더한다() {
        // given
        Distance distance1 = new Distance(2);
        Distance distance2 = new Distance(1);

        // when
        Distance newDistance = distance1.plus(distance2);

        // then
        assertThat(newDistance).isEqualTo(new Distance(3));
    }

    @Test
    void 거리가_같은_경우_예외를_일으킨다() {
        // given
        Distance distance1 = new Distance(1);
        Distance distance2 = new Distance(1);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                distance1.minus(distance2)
        );
    }

    @Test
    void 대상_거리가_비교하는_거리보다_작은_경우_예외를_일으킨다() {
        // given
        Distance distance1 = new Distance(1);
        Distance distance2 = new Distance(2);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                distance1.minus(distance2)
        );
    }
}
