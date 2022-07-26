package nextstep.subway.unit;

import nextstep.subway.domain.PathFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PathFinderTest {


    @DisplayName("출발역과 도착역이 같은 경우 예외 처리")
    @Test
    void sameSourceAndTargetStation() {
        assertThatIllegalArgumentException().isThrownBy(() -> PathFinder.of(1L, 1L));
    }
}
