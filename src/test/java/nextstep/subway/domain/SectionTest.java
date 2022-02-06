package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class SectionTest {

    @DisplayName("구간 생성 간 거리 예외")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void createSection(final int distance) {
        // given
        // when
        // then
        assertThatIllegalArgumentException().isThrownBy(() -> new Section(new Line(), new Station(), new Station(), distance))
                .withMessage("구간의 거리는 0 초과 이어야 합니다.");
    }
}