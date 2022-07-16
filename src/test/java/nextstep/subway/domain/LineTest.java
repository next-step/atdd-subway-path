package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @Test
    @DisplayName("Line이 정상적으로 생성된다.")
    void createLineTest() {
        assertDoesNotThrow(() -> new Line("4호선", "blue"));
    }

    @Test
    @DisplayName("구간이 정상적으로 추가된다.")
    void addSectionTest() {
        Line line = new Line("4호선", "blue");
        assertThat(line.getSections()).isEmpty();

        line.addSection(new Section(line, new Station("중앙역"), new Station("한대앞역"), 10));
        assertThat(line.getSections()).hasSize(1);
    }
}