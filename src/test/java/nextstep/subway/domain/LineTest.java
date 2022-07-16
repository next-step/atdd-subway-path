package nextstep.subway.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @Test
    @DisplayName("Line이 정상적으로 생성된다.")
    void createLineTest() {
        assertDoesNotThrow(() -> new Line("4호선", "blue"));

    }
}